package com.modutaxi.api.domain.chatmessage.dto;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private Long roomId;
    private MessageType messageType;
    private String content;
    private String sender;

    public static ChatMessageResponseDto toDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .roomId(chatMessage.getRoom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sender(chatMessage.getSender())
                .build();
    }
}
