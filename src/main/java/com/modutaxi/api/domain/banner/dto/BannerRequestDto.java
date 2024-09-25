package com.modutaxi.api.domain.banner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class BannerRequestDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class CreateBannerRequest {
        @Schema(example = "String", description = "배너 이미지 url")
        private String imageUrl;
        @Schema(example = "String", description = "배너 링크 url")
        private String linkUrl;
    }
}
