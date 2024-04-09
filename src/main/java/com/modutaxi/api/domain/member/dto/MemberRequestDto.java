package com.modutaxi.api.domain.member.dto;

import com.modutaxi.api.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SignUpRequest {
        private String key;
        private String name;
        private Gender gender;
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String accessToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendMailCertificationRequest {
        @Schema(example = "12181659@inha.edu", description = "인증 메일을 받을 이메일 주소")
        private String mailAddress;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfirmMailCertificationReqeust {
        @Schema(example = "12345", description = "인증 메일로 받은 인증 코드")
        private String certCode;
    }
}
