package com.modutaxi.api.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @AllArgsConstructor
    public static class MailResponse {
        @Schema(example = "true", description = "메일 API 성공 여부")
        private Boolean isConfirm;
    }

    @Getter
    @AllArgsConstructor
    public static class MembershipResponse {
        private boolean existent;
        private String key;
    }
}
