package com.modutaxi.api.domain.chat.service;

import com.modutaxi.api.domain.chat.MessageType;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ChatService {
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 채팅방에 메시지 발송
     */
    public void sendChatMessage(ChatMessage chatMessage) {

        if (chatMessage.getType().equals(MessageType.JOIN)) {
            chatMessage.setContent(chatMessage.getSender() + "님이 들어왔습니다.");
        } else if (chatMessage.getType().equals(MessageType.LEAVE)) {
            chatMessage.setContent(chatMessage.getSender() + "님이 나갔습니다.");
        }
        System.out.println("어디로갈까요? = " + channelTopic.getTopic() + chatMessage.getRoomId() );

        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }

}
