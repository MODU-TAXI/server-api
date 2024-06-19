package com.modutaxi.api.domain.participant.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
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
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
    private static final int FULL_MEMBER = 4;
    /**
     * 방 참가 수락
     */
    @Transactional
    public RoomWaitingMapper.ApplyResponse acceptForParticipate(Member member, Long roomId, Long memberId){
        Room room = roomRepository.findById(roomId).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM));

        Member participant = memberRepository.findByIdAndStatusTrue(memberId)
                .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        if(redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId.toString())!=null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_IN);
        }

        //방 매니저가 아닌 사용자의 허락은 에러
        if(!room.getRoomManager().getId().equals(member.getId())){
            throw new BaseException(ParticipateErrorCode.YOUR_NOT_ROOM_MANAGER);
        }

        //방 자체 상태가 COMPLETE면 에러
        if(room.getRoomStatus().equals(RoomStatus.COMPLETE)){
            throw new BaseException(ParticipateErrorCode.PARTICIPATE_NOT_ALLOW);
        }

        // 대기열에 특정 사용자가 존재하지 않으면 에러
        if(!roomWaitingRepository.existsByMemberAndRoom(member, room)){
            throw new BaseException(ParticipateErrorCode.USER_NOT_IN_ROOM_WAITING);
        }

        //이미 유저가 해당 방에 존재할 때
        if(participantRepository.existsByMemberAndRoom(member, room)){
            throw new BaseException(ParticipateErrorCode.USER_ALREADY_IN_ROOM);
        }

        if(room.getCurrentHeadcount() == FULL_MEMBER){
            throw new BaseException(ParticipateErrorCode.ROOM_IS_FULL);
        }

        //fcm구독
        fcmService.subscribe(participant.getId(), roomId);

        //대기열에서 제거
        roomWaitingRepository.deleteByMemberAndRoom(member, room);

        //채팅방에 저장
        participantRepository.save(ParticipantMapper.toEntity(member,room));

        //매핑 정보 저장
        ChatRoomMappingInfo chatRoomMappingInfo = new ChatRoomMappingInfo(roomId.toString(), participant.getNickname());
        redisChatRoomRepositoryImpl.setUserEnterInfo(memberId.toString(), chatRoomMappingInfo);
        room.plusCurrentHeadCount();

        //참가 수락되었다는 메세지 본인에게 전송
        fcmService.sendPermitParticipate(participant, roomId.toString());

        //방 팀원들에게 참가했다는 메세지 보내기
        chatService.sendChatMessage(new ChatMessageRequestDto(
                roomId, MessageType.JOIN, participant.getNickname() + "님이 들어왔습니다.",
                chatRoomMappingInfo.getNickname(), memberId.toString(), LocalDateTime.now(), ""));

        return new RoomWaitingMapper.ApplyResponse(true);
    }
}
