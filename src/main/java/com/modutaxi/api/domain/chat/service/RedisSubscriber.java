package com.modutaxi.api.domain.chat.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.StompErrorCode;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber {

    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messageSendingOperations;

    public void sendMessage(String publishMessage) {
        try {
            ChatMessageResponse chatMessageResponse;
            chatMessageResponse = objectMapper.readValue(publishMessage, ChatMessageResponse.class);

            // Log each field of ChatMessageResponse
            log.info("Room ID: {}", chatMessageResponse.getRoomId());
            log.info("Message Type: {}", chatMessageResponse.getMessageType());
            log.info("Content: {}", chatMessageResponse.getContent());
            log.info("Sender: {}", chatMessageResponse.getSender());
            log.info("Member ID: {}", chatMessageResponse.getMemberId());
            log.info("Date and Time: {}", chatMessageResponse.getDateTime());

            messageSendingOperations.convertAndSend("/sub/chat/" + chatMessageResponse.getRoomId(),
                chatMessageResponse);
        } catch (JsonProcessingException e) {
            throw new BaseException(StompErrorCode.FAIL_SEND_MESSAGE);
        }
    }
}
