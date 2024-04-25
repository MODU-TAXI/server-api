package com.modutaxi.api.domain.onboarding.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OnboardingRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OnboardingRequest {
        @Schema(example = "1", description = "설문조사 ID (현재 존재하는 설문조사 ID는 1~2입니다.)")
        private int questionId;

        @Schema(example = "true", description = "1번 응답을 체크했다면 true, 체크하지 않았다면 false")
        private boolean answer1;
        @Schema(example = "true", description = "2번 응답을 체크했다면 true, 체크하지 않았다면 false")
        private boolean answer2;
        @Schema(example = "true", description = "3번 응답을 체크했다면 true, 체크하지 않았다면 false")
        private boolean answer3;

        @Schema(example = "true", description = "기타에 체크했다면 true, 체크하지 않았다면 false")
        private boolean etc;
        @Schema(example = "어쩌구 저쩌구해서 이러쿵 저러쿵 했습니다.", description = "etc가 true일 때는 기타 사유를 넣어주시고, etc가 false라면 null이 아닌 블랭크로 전송해주세요.")
        private String etcContent;
    }

}
