package com.modutaxi.api.domain.chat.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class ChatResponseDto {
    @Getter
    @AllArgsConstructor
    @ToString
    public static class ChatMappingResponse {
        private String roomId;
        private String memberId;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class EnterableResponse {
        private Boolean isEnterable;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class DeleteResponse {
        private Boolean isDeleted;
    }
}