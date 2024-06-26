package com.modutaxi.api.domain.participant.service;

import static com.modutaxi.api.common.constants.ServerConstants.FULL_MEMBER;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import com.modutaxi.api.domain.alarm.service.RegisterAlarmService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.participant.mapper.ParticipantMapper;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.roomwaiting.dto.RoomWaitingResponseDto.ApplyResponse;
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RegisterParticipantService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ParticipantRepository participantRepository;
    private final RoomWaitingRepository roomWaitingRepository;
    private final FcmService fcmService;
    private final ChatService chatService;
    private final RegisterAlarmService registerAlarmService;

    /**
     * 방 참가 수락
     */
    @Transactional
    public ApplyResponse acceptForParticipate(Member member, Long roomId, Long memberId) {
        Room room = roomRepository.findById(roomId).orElseThrow(
            () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        Member participant = memberRepository.findByIdAndStatusTrue(memberId)
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        log.info("{}번 방에 {}번 참가자의 대기요청이 들어왔습니다.", roomId, participant.getId());

        if (redisChatRoomRepositoryImpl.findChatInfoByMemberId(participant.getId().toString())
            != null) {
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        //방 매니저가 아닌 사용자의 허락은 에러
        if (!room.getRoomManager().getId().equals(member.getId())) {
            throw new BaseException(ParticipateErrorCode.YOUR_NOT_ROOM_MANAGER);
        }

        //방 자체 상태가 COMPLETE면 에러
        if (room.getRoomStatus().equals(RoomStatus.COMPLETE)) {
            log.error("{}번 방은 매칭 완료된 방입니다.", roomId);
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }

        // 대기열에 특정 사용자가 존재하지 않으면 에러
        if (!roomWaitingRepository.existsByMemberAndRoom(participant, room)) {
            log.error("{}번 사용자가 {}번 room 대기열에 존재하지 않습니다.", participant.getId(), room.getId());
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM_WAITING);
        }

        //이미 유저가 해당 방에 존재할 때
        if (participantRepository.existsByMemberAndRoom(participant, room)) {
            log.error("{}번 사용자가 {}번 room 참가자 리스트에 이미 존재합니다.", participant.getId(), room.getId());
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        if (room.getCurrentHeadcount() == FULL_MEMBER) {
            throw new BaseException(ParticipateErrorCode.ROOM_IS_FULL);
        }

        //fcm구독
        fcmService.subscribe(participant.getId(), roomId);

        //대기열에서 제거
        roomWaitingRepository.deleteByMemberAndRoom(participant, room);

        //참가열에 저장
        participantRepository.save(ParticipantMapper.toEntity(participant, room));

        //매핑 정보 저장
        ChatRoomMappingInfo chatRoomMappingInfo = new ChatRoomMappingInfo(roomId.toString(),
            participant.getNickname());
        redisChatRoomRepositoryImpl.setUserEnterInfo(participant.getId().toString(),
            chatRoomMappingInfo);
        room.plusCurrentHeadCount();

        //참가 수락되었다는 메세지 본인에게 전송 & 알림 남기기
        fcmService.sendPermitParticipate(participant, roomId.toString());
        registerAlarmService.registerAlarm(AlarmType.MATCHING_SUCCESS, roomId, participant.getId());

        //방 팀원들에게 참가했다는 메세지 보내기
        chatService.sendChatMessage(new ChatMessageRequestDto(
            roomId, MessageType.JOIN, participant.getNickname() + "님이 들어왔습니다.",
            chatRoomMappingInfo.getNickname(), memberId.toString(), LocalDateTime.now(), ""));

        return new ApplyResponse(true);
    }
}
