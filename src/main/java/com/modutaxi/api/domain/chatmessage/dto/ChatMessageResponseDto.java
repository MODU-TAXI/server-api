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
    private String memberId;

    public static ChatMessageResponseDto entityToDto(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .roomId(chatMessage.getRoom().getId())
                .messageType(chatMessage.getMessageType())
                .content(chatMessage.getContent())
                .sender(chatMessage.getSender())
                .build();
    }

    public static ChatMessageResponseDto requestDtoToResponseDto(ChatMessageRequestDto chatMessageRequestDto, String memberId) {
        return ChatMessageResponseDto.builder()
                .roomId(chatMessageRequestDto.getRoomId())
                .messageType(chatMessageRequestDto.getType())
                .content(chatMessageRequestDto.getContent())
                .sender(chatMessageRequestDto.getSender())
                .memberId(memberId)
                .build();
    }
}
