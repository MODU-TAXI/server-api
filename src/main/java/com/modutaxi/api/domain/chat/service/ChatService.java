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
import com.modutaxi.api.domain.chat.repository.RedisChatRoomRepositoryImpl;
import com.modutaxi.api.domain.chatmessage.mapper.ChatMessageMapper;
import com.modutaxi.api.domain.chatmessage.repository.ChatMessageRepository;
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
    private final RedisChatRoomRepositoryImpl redisChatRoomRepositoryImpl;
    private final RoomRepository roomRepository;
    private final FcmService fcmService;
    private final ChatMessageRepository chatMessageRepository;

    /**
     * 채팅방에 메시지 pub
     */
    public void sendChatMessage(ChatMessageRequestDto chatMessageRequestDto) {
        redisTemplate.convertAndSend(channelTopic.getTopic(),
                ChatMessageMapper.toDto(chatMessageRequestDto));

        fcmService.sendChatMessage(chatMessageRequestDto);

        chatMessageRepository.save(ChatMessageMapper.toEntity(chatMessageRequestDto));
    }

    public EnterableResponse chatRoomEnterPossible(Member member, Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new BaseException(
                RoomErrorCode.EMPTY_ROOM));

        if (redisChatRoomRepositoryImpl.findChatInfoByMemberId(member.getId().toString()) != null) {
            throw new BaseException(RoomErrorCode.ALREADY_IN_ROOM);
        }


        if (room.getCurrentHeadcount() >= 4) {
            return new EnterableResponse(false);
        }

        return new EnterableResponse(true);
    }

    public ChatMappingResponse getChatRoomInfo(Member member) {
        ChatRoomMappingInfo chatInfo = redisChatRoomRepositoryImpl.findChatInfoByMemberId(member.getId().toString());
        if (chatInfo == null) {
            return new ChatMappingResponse("null", member.getId().toString());
        }
        return new ChatMappingResponse(chatInfo.getRoomId(), member.getId().toString());
    }
}
