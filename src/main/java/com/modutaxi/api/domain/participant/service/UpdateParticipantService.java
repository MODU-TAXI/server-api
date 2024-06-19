package com.modutaxi.api.domain.participant.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto;
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chat.service.ChatService;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class UpdateParticipantService {
    private final MemberRepository memberRepository;
    private final RoomRepository roomRepository;
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final ChatService chatService;
    private final FcmService fcmService;
    private final ParticipantRepository participantRepository;
    /**
     * 방 퇴장 로직
     */
    @Transactional
    public ChatResponseDto.DeleteResponse leaveRoomAndDeleteChatRoomInfo(Long memberId) {
        Member member = memberRepository.findByIdAndStatusTrue(memberId).orElseThrow(()
                -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        ChatRoomMappingInfo chatRoomMappingInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(memberId.toString());

        // 연결된 방이 없다면 에러
        if (chatRoomMappingInfo == null) {
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_OUT);
        }

        // 존재하는 방이 없다면 에러
        Room room = roomRepository.findById(Long.valueOf(chatRoomMappingInfo.getRoomId())).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM)
        );

        // 퇴장하려는 대상이 방장인 경우 에러
        if (room.getRoomManager().getId().equals(memberId)) {
            throw new BaseException(RoomErrorCode.MANAGER_CAN_ONLY_DELETE);
        }
        // 기존 방 인원 감소
        room.minusCurrentHeadCount();

        // 기존 방에 클라이언트 퇴장 메시지 발송
        ChatMessageRequestDto leaveMessage = new ChatMessageRequestDto(Long.valueOf(
                chatRoomMappingInfo.getRoomId()), MessageType.LEAVE,
                chatRoomMappingInfo.getNickname() + "님이 나갔습니다.",
                chatRoomMappingInfo.getNickname(), member.getId().toString(), LocalDateTime.now(), "");
        chatService.sendChatMessage(leaveMessage);


        //참여자 정보 삭제
        participantRepository.deleteParticipantByMemberAndRoom(member, room);

        //chatRoomInfo 삭제
        redisChatRoomRepositoryImpl.removeUserByMemberIdEnterInfo(member.getId().toString());

        //해당 방에 대한 Fcm 구독 해제
        fcmService.unsubscribe(member.getId(), room.getId());
        return new ChatResponseDto.DeleteResponse(true);
    }

}
