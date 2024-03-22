package com.modutaxi.api.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }
}
