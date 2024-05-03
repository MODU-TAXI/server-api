package com.modutaxi.api.domain.member.dto;

import com.modutaxi.api.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RefreshTokenResponse {
        private TokenResponse tokenResponse;
        private MemberInfoResponse memberInfoResponse;
    }

    @Getter
    @AllArgsConstructor
    public static class CertificationResponse {
        @Schema(example = "true", description = "API 성공 여부")
        private Boolean isConfirm;
    }

    @Getter
    @AllArgsConstructor
    public static class MembershipResponse {
        private boolean existent;
        private String key;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class MemberInfoResponse {
        private Long id;
        private String name;
        private Gender gender;
        private String phoneNumber;
        private String email;
        private double score;
    }
}
