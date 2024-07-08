package com.modutaxi.api.domain.paymentmember.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.PaymentErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import com.modutaxi.api.domain.alarm.service.RegisterAlarmService;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.UpdatePaymentMemberResponse;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentmember.repository.PaymentMemberRepository;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import com.modutaxi.api.domain.paymentroom.service.GetPaymentRoomService;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.room.service.UpdateRoomService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdatePaymentMemberService {

    private final GetPaymentRoomService getPaymentRoomService;
    private final RegisterAlarmService registerAlarmService;
    private final ChatService chatService;
    private final UpdateRoomService updateRoomService;

    private final PaymentMemberRepository paymentMemberRepository;
    private final RoomRepository roomRepository;

    public UpdatePaymentMemberResponse updatePaymentMembersToComplete(Member member,
        Long roomId) {
        // 1. 정산방 가져오기
        PaymentRoom paymentRoom = getPaymentRoomService.getPaymentRoomByRoomId(roomId);
        // 2. 정산 멤버 가져오기
        PaymentMember paymentMember = paymentMemberRepository.findByPaymentRoomAndMember(
                paymentRoom, member)
            .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_PAYMENT_MEMBER));
        // 3. 이미 정산한 멤버 또는 정산 대상 멤버가 아닐 경우 밸리데이션
        checkPaymentMemberStatus(paymentMember);

        // 4. 정산 완료 처리
        paymentMember.setStatusComplete();
        // 5. 메시지 전송
        ChatMessageRequestDto paymentCompleteMessageRequestDto =
            new ChatMessageRequestDto(
                roomId, MessageType.PAYMENT_COMPLETE,
                "정산 완료했어요!",
                member.getNickname(),
                member.getId().toString(),
                LocalDateTime.now(), "");
        chatService.sendChatMessage(paymentCompleteMessageRequestDto);

        // 모든 멤버 정산 완료 시 메시지 전송
        checkAllComplete(roomId, paymentRoom);

        return new UpdatePaymentMemberResponse(true);
    }

    /**
     * 이미 정산한 멤버 또는 정산 대상 멤버가 아닐 경우 예외가 발생합니다.
     */
    private void checkPaymentMemberStatus(PaymentMember paymentMember) {
        if (paymentMember.getStatus() == PaymentMemberStatus.COMPLETE) {
            throw new BaseException(PaymentErrorCode.ALREADY_PAYMENT_MEMBER);
        } else if (paymentMember.getStatus() == PaymentMemberStatus.DEACTIVATED) {
            throw new BaseException(PaymentErrorCode.NOT_PAYMENT_MEMBER);
        }
    }

    /**
     * 모든 멤버 정산 완료 시 메시지를 전송합니다.
     */
    private void checkAllComplete(Long roomId, PaymentRoom paymentRoom) {
        int countsOfIncomplete = paymentMemberRepository.countByPaymentRoomAndStatus(
            paymentRoom, PaymentMemberStatus.INCOMPLETE);
        // 1. 정산을 완료하지 않은 멤버가 더 이상 없다면
        if (countsOfIncomplete == 0) {
            Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));

            Member manager = room.getRoomManager();

            updateRoomService.deleteRoom(manager, roomId);

            // 방장에게 모든 정산 완료를 알리는 메시지 전송
            ChatMessageRequestDto paymentCompleteMessageRequestDto =
                new ChatMessageRequestDto(
                    roomId, MessageType.PAYMENT_ALL_COMPLETE,
                    "모든 정산이 완료됐어요!",
                    MessageType.PAYMENT_ALL_COMPLETE.getSenderName(),
                    manager.getId().toString(),
                    LocalDateTime.now(), "");
            chatService.sendChatMessage(paymentCompleteMessageRequestDto);

            // 알림 저장
            registerAlarmService.registerAlarm(AlarmType.PAYMENT_ALL_COMPLETE, roomId,
                manager.getId());
        }
    }
}
