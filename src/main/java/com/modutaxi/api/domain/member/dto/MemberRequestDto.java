package com.modutaxi.api.domain.member.dto;

import com.modutaxi.api.domain.member.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequest {
        private String snsId;
        private String name;
        private Gender gender;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CheckNameRequest {
        private String name;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String accessToken;
    }
}
