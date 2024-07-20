package com.modutaxi.api.domain.member.dto;

import com.modutaxi.api.domain.member.entity.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class MemberResponseDto {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class TokenResponse {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class TokenAndMemberResponse {
        private TokenResponse tokenResponse;
        private MemberInfoResponse memberInfoResponse;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class CertificationResponse {
        @Schema(example = "true", description = "API 성공 여부")
        private Boolean isConfirm;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class MemberInfoResponse {
        private Long id;
        private String name;
        private String nickname;
        private Gender gender;
        private String phoneNumber;
        private String email;
        private String imageUrl;
        private int matchingCount;
        private boolean blocked;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class NicknameResponse {
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class MemberProfileResponse {
        @Schema(example = "1", description = "조회한 멤버의 Id")
        private Long id;
        @Schema(example = "헤일", description = "닉네임")
        private String nickname;
        @Schema(example = "3", description = "이용 횟수")
        private int matchingCount;
        @Schema(example = "true", description = "학생 인증 여부")
        private boolean isCertified;
        @Schema(example = "-", description = "프로필 이미지 S3 링크")
        private String imageUrl;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class UpdateProfileResponse {
        @Schema(description = "변경한 이름")
        private String name;
        @Schema(description = "변경한 성별")
        private Gender gender;
        @Schema(description = "변경한 전화번호")
        private String phoneNumber;
        @Schema(description = "변경한 프로필 이미지의 S3 링크")
        private String ImageUrl;
    }
}
