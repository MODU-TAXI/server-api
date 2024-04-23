package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.domain.chat.MessageType;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import com.modutaxi.api.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPublisher {

    private final ChatRoomRepository chatRoomRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(RedisSubscriber redisSubscriber, ChatMessage chatMessage) {
        if (chatMessage.getType().equals(MessageType.JOIN)) {
            chatRoomRepository.enterChatRoom(redisSubscriber, chatMessage.getRoomId());
            chatMessage.setContent(chatMessage.getSender() + "님이 들어왔습니다.");

        } else if (chatMessage.getType().equals(MessageType.LEAVE)) {
            chatRoomRepository.leaveChatRoom(redisSubscriber);
            chatMessage.setContent(chatMessage.getSender() + "님이 나갔습니다.");
        } else if(chatMessage.getType().equals(MessageType.CHAT)){
            chatMessage.setContent(chatMessage.getSender() + "님 : "+ chatMessage.getContent());
        }

        redisTemplate.convertAndSend("/sub/chat/" + chatMessage.getRoomId(), chatMessage);
    }
}
