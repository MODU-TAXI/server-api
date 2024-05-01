package com.modutaxi.api.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messageSendingOperations;

    public void sendMessage(String publishMessage) {
        try {
            ChatMessageResponseDto chatMessageResponseDto;
            chatMessageResponseDto = objectMapper.readValue(publishMessage, ChatMessageResponseDto.class);
            messageSendingOperations.convertAndSend("/sub/chat/" + chatMessageResponseDto.getRoomId(),
                    chatMessageResponseDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("실패해쪙");
        }
    }
}
