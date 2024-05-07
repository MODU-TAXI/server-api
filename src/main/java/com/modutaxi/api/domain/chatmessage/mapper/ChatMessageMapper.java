package com.modutaxi.api.domain.chatmessage.mapper;

import com.modutaxi.api.domain.chatmessage.dto.ChatMessageRequestDto;
import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto.ChatMessageResponse;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import com.modutaxi.api.domain.room.entity.Room;

public class ChatMessageMapper {

    public static ChatMessage toEntity(ChatMessageRequestDto messageRequestDto, Room room){
        return ChatMessage.builder()
                .room(room)
                .messageType(messageRequestDto.getType())
                .content(messageRequestDto.getContent())
                .sender(messageRequestDto.getSender())
                .memberId(messageRequestDto.getMemberId())
                .dateTime(messageRequestDto.getDateTime())
                .build();
    }

    public static ChatMessageResponse toDto(ChatMessage chatMessage) {
        return ChatMessageResponse.builder()
            .roomId(chatMessage.getRoom().getId())
            .messageType(chatMessage.getMessageType())
            .content(chatMessage.getContent())
            .sender(chatMessage.getSender())
            .memberId(chatMessage.getMemberId())
            .dateTime(chatMessage.getDateTime())
            .build();
    }

    public static ChatMessageResponse toDto(ChatMessageRequestDto chatMessageRequestDto) {
        return ChatMessageResponse.builder()
            .roomId(chatMessageRequestDto.getRoomId())
            .messageType(chatMessageRequestDto.getType())
            .content(chatMessageRequestDto.getContent())
            .sender(chatMessageRequestDto.getSender())
            .memberId(chatMessageRequestDto.getMemberId())
            .dateTime(chatMessageRequestDto.getDateTime())
            .build();
    }
}
