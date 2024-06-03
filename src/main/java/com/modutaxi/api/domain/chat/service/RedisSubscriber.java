package com.modutaxi.api.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.exception.StompException;
import com.modutaxi.api.common.exception.errorcode.StompErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponse;
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
            ChatMessageResponse chatMessageResponse;
            chatMessageResponse = objectMapper.readValue(publishMessage, ChatMessageResponse.class);
            messageSendingOperations.convertAndSend("/sub/chat/" + chatMessageResponse.getRoomId(),
                    chatMessageResponse);
        } catch (JsonProcessingException e) {
            throw new StompException(StompErrorCode.FAIL_SEND_MESSAGE);
        }
    }
}
