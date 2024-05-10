package com.modutaxi.api.domain.chat.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ChatResponseDto {
    @Getter
    @AllArgsConstructor
    public static class ChatMappingResponse {
        private String roomId;
        private String memberId;
    }

    @Getter
    @AllArgsConstructor
    public static class EnterableResponse {
        private Boolean isEnterable;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteResponse {
        private Boolean isDeleted;
    }
}