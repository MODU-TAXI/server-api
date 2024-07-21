package com.modutaxi.api.domain.chatmessage.dto;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class ChatMessageResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
    @ToString
    public static class ChatMessageResponse {
        private Long roomId;
        private MessageType messageType;
        private String content;
        private String sender;
        private String memberId;
        private LocalDateTime dateTime;
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ChatMessageResponseList {
        private List<ChatMessageResponse> messages;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class DeleteResponse {
        private Boolean isDeleted;
    }
}
