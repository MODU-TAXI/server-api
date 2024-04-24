package com.modutaxi.api.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.domain.chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messageSendingOperations;

    public void sendMessage(String publishMessage) {
        System.out.println("subcriber ok");
        try {
            ChatMessage chatMessage;
            chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            messageSendingOperations.convertAndSend("/sub/chat/" + chatMessage.getRoomId(), chatMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("실패해쪙");
        }
    }
}
