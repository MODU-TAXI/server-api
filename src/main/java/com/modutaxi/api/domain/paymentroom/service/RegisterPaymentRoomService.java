package com.modutaxi.api.domain.paymentroom.service;

import static com.modutaxi.api.domain.paymentroom.mapper.PaymentRoomMapper.toEntity;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.PaymentErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.history.mapper.HistoryMapper;
import com.modutaxi.api.domain.history.repository.HistoryRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentmember.service.RegisterPaymentMemberService;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomRequestDto.MemberId;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomRequestDto.PaymentRoomRequest;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.RegisterPaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import com.modutaxi.api.domain.paymentroom.repository.PaymentRoomRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RegisterPaymentRoomService {

    private final PaymentRoomRepository paymentRoomRepository;
    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final HistoryRepository historyRepository;
    private final RoomWaitingRepository roomWaitingRepository;
    private final ParticipantRepository participantRepository;
    private final RegisterPaymentMemberService registerPaymentMemberService;
    private final ChatService chatService;

    public RegisterPaymentRoomResponse register(Member member, PaymentRoomRequest request) {
        // 0. 방 가져오기
        Room room = roomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));
        // 1. 방장인지 확인
        checkManager(room.getRoomManager().getId(), member.getId());
        // 2. 계좌 올바른거 가져오기
        Account account = accountRepository.findById(request.getAccountId())
            .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_ACCOUNT));
        // 3. 정산 생성 완료
        PaymentRoom paymentRoom = toEntity(request.getRoomId(), request.getTotalCharge(),
            account.getId());
        paymentRoomRepository.save(paymentRoom);
        // 4. 정산 멤버 등록
        registerPaymentMemberList(room.getId(), room.getRoomManager().getId(), paymentRoom,
            request.getParticipantList(), request.getNonParticipantList());

        // 5. 이용 내역 저장
        request.getParticipantList().forEach(participant -> {
            Member participantMember = memberRepository.findById(participant.getId())
                .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_ACCOUNT));
            History history = HistoryMapper.toEntity(room, participantMember, paymentRoom.getTotalCharge(),
                paymentRoom.getTotalCharge() / request.getParticipantList().size());
            historyRepository.save(history);
            log.info("{}번 ID Member 이용내역 저장", participantMember.getId());
        });

        // 6. 방장의 이름으로 정산 해주세요~ 메시지 전송
        String content = "목적지에 도착했어요,\n'정산하기'를 눌러주세요!";
        ChatMessageRequestDto chatMessageRequestDto =
            new ChatMessageRequestDto(room.getId(), MessageType.PAYMENT_REQUEST_COMPLETE, content,
                room.getRoomManager().getNickname(), room.getRoomManager().getId().toString(),
                LocalDateTime.now(), room.getRoomManager().getImageUrl());

        chatService.sendChatMessage(chatMessageRequestDto);

        return new RegisterPaymentRoomResponse(paymentRoom.getId());
    }

    private void checkManager(Long managerId, Long memberId) {
        if (!managerId.equals(memberId)) {
            throw new BaseException(RoomErrorCode.NOT_ROOM_MANAGER);
        }
    }

    /**
     * PaymentRoom에 속하는 모든 정산 멤버들을 등록합니다.
     * PaymentMemberStatus: INCOMPLETE(정산 미완료), COMPLETE(정산 완료), DEACTIVATED(정산 대상 미포함)
     */
    private void registerPaymentMemberList(Long roomId, Long managerId, PaymentRoom paymentRoom,
        List<MemberId> participantList, List<MemberId> nonParticipantList) {
        participantList.forEach(
            participant ->
                registerPaymentMember(roomId, participant.getId(), paymentRoom,
                    (Objects.equals(participant.getId(), managerId))
                        ? PaymentMemberStatus.COMPLETE      // 방장이라면 정산 완료로 초기화
                        : PaymentMemberStatus.INCOMPLETE)   // 멤버라면 정산 미완료로 초기화
        );

        nonParticipantList.forEach(
            nonParticipant ->
                registerPaymentMember(roomId, nonParticipant.getId(), paymentRoom,
                    PaymentMemberStatus.DEACTIVATED)
        );
    }

    /**
     * 정산 멤버 1명을 PaymentMember Entity로 등록합니다.
     */
    private void registerPaymentMember(Long roomId, Long memberId, PaymentRoom paymentRoom,
        PaymentMemberStatus status) {
        // 0. member validation & room validation
        Member member = memberRepository.findByIdAndStatusTrue(memberId)
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        // 1. 방에 들어가있는지 확인
        boolean isInRoom = participantRepository.existsByMemberAndRoom(member, room);
        if (!isInRoom) {
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM);
        }
        // 2. 이용 횟수 및 노쇼 횟수 카운팅
        countMatchingOrNoShow(member, status);
        // 3. 정산 멤버(PaymentMember) 등록
        registerPaymentMemberService.register(paymentRoom, member, status);
    }

    /**
     * 이용 횟수 및 노쇼 횟수를 카운팅합니다.
     * DEACTIVATED(정산 대상 미포함) 상태라면 노쇼 횟수 증가
     * INCOMPLETE(정산 미완료), COMPLETE(정산 완료) 상태라면 이용 횟수 증가
     */
    private void countMatchingOrNoShow(Member member, PaymentMemberStatus status) {
        if (status == PaymentMemberStatus.DEACTIVATED) {
            member.plusOneNoShowCount();
        } else {
            member.plusOneMatchingCount();
        }
    }
}
