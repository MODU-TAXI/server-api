package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.common.fcm.FcmService;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.ChatMappingResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.EnterableResponse;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chat.ChatRoomMappingInfo;
import com.modutaxi.api.domain.chat.ChatNickName;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomRepository roomRepository;
    private final FcmService fcmService;
    /**
     * 채팅방에 메시지 발송할 수 있도록
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {

        fcmService.sendChatMessage(chatMessageRequestDto);

        redisTemplate.convertAndSend(channelTopic.getTopic(),
            ChatMessageMapper.toDto(chatMessageRequestDto));
    }

    public DeleteResponse deleteChatRoomInfo(Member member){
        ChatRoomMappingInfo chatRoomMappingInfo = chatRoomRepository.findChatInfoByMemberId(member.getId().toString());

        if(chatRoomMappingInfo == null){
            throw new BaseException(ChatErrorCode.ALREADY_ROOM_OUT);
        }

        Room room = roomRepository.findById(Long.valueOf(chatRoomMappingInfo.getRoomId())).orElseThrow(
                () -> new BaseException(RoomErrorCode.EMPTY_ROOM)
        );

        if(room.getRoomManager().equals(member)){
            throw new BaseException(RoomErrorCode.JUST_DELETE);
        }

        int count = ChatNickName.valueOf(chatRoomMappingInfo.getNickname()).getValue();
        // 클라이언트 퇴장 메시지 발송한다.
        ChatMessageRequestDto leaveMessage = new ChatMessageRequestDto(Long.valueOf(
                chatRoomMappingInfo.getRoomId()), MessageType.LEAVE,
            chatRoomMappingInfo.getNickname() + "님이 나갔습니다.",
                chatRoomMappingInfo.getNickname(), member.getId().toString(), LocalDateTime.now());

        sendChatMessage(leaveMessage);

        chatRoomRepository.removeUserByMemberIdEnterInfo(member.getId().toString());
        chatRoomRepository.minusUserCount(chatRoomMappingInfo.getRoomId(), count);

        return new DeleteResponse(true);
    }

    public EnterableResponse chatRoomEnterPossible(Member member, Long roomId){
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException(
                RoomErrorCode.EMPTY_ROOM));

        if(chatRoomRepository.findChatInfoByMemberId(member.getId().toString()) != null){
            throw new BaseException(RoomErrorCode.ALREADY_IN_ROOM);
        }

        if(chatRoomRepository.getUserCount(roomId.toString()) >= 15){
            return new EnterableResponse(false);
        }

        return new EnterableResponse(true);
    }

    public ChatMappingResponse getChatRoomInfo(Member member){
        ChatRoomMappingInfo chatInfo = chatRoomRepository.findChatInfoByMemberId(member.getId().toString());
        if(chatInfo == null){
            return new ChatMappingResponse("null",member.getId().toString());
        }
        return new ChatMappingResponse(chatInfo.getRoomId(),member.getId().toString());
    }
}
