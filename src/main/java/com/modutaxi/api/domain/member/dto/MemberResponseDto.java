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
    public static class TokenAndMemberResponse {
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
        private String key;
    }

    @Getter
    @Builder
    @AllArgsConstructor
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
    public static class NicknameResponse {
        private String nickname;
    }

    @Getter
    @Builder
    @AllArgsConstructor
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
