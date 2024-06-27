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
        private String fcmToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LoginRequest {
        private String accessToken;
        private String fcmToken;
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

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendSmsCertificationRequestWithSignupKey {
        @Schema(description = "회원가입 시 발급받은 키")
        private String key;
        @Schema(description = "인증번호를 받을 전화번호")
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SendSmsCertificationRequestWithJwt {
        @Schema(description = "인증번호를 받을 전화번호")
        private String phoneNumber;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfirmSmsCertificationReqeustWithSignupKey {
        @Schema(description = "회원가입 시 발급받은 키")
        private String key;
        @Schema(description = "인증번호를 받은 전화번호")
        private String phoneNumber;
        @Schema(description = "인증번호")
        private String certificationCode;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConfirmSmsCertificationReqeustWithJwt {
        @Schema(description = "인증번호를 받은 전화번호")
        private String phoneNumber;
        @Schema(description = "인증번호")
        private String certificationCode;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NicknameRequest {
        @Schema(description = "등록할 닉네임")
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateProfileRequest {
        @Schema(description = "변경할 이름")
        private String name;
        @Schema(description = "변경할 성별")
        private Gender gender;
        @Schema(description = "변경할 전화번호")
        private String phoneNumber;
        @Schema(description = "변경할 프로필 이미지의 S3 링크")
        private String imageUrl;
    }
}
