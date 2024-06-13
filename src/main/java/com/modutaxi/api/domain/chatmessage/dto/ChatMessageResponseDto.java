package com.modutaxi.api.domain.chatmessage.dto;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ChatMessageResponseDto {

    @Getter
    @AllArgsConstructor
    @Builder
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
    public static class ChatMessageResponseList {
        private List<ChatMessageResponse> messages;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteResponse {
        private Boolean isDeleted;
    }
}
