package com.modutaxi.api.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class MemberRequestDto {

    @Getter
    @AllArgsConstructor
    public static class SignUpRequest {
        private String email;
        private String name;
    }
}
