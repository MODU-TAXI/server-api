package com.modutaxi.api.domain.member.dto;

import com.modutaxi.api.domain.member.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberRequestDto {

    @Getter
    @AllArgsConstructor
    public static class SignUpRequest {
        private Long snsId;
        private String name;
        private Gender gender;
    }

    @Getter
    @AllArgsConstructor
    public static class CheckNameRequest {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class LoginRequest {
        private String accessToken;
    }
}
