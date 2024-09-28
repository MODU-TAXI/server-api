package com.modutaxi.api.domain.banner.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class BannerResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class BannerResponse {
        @Schema(example = "1", description = "배너 id")
        private Long id;
        @Schema(example = "String", description = "배너 이미지 url")
        private String imageUrl;
        @Schema(example = "String", description = "배너 링크 url")
        private String linkUrl;
        @Schema(example = "false", description = "배너 활성화 여부")
        private boolean active;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class BannerResponseList {
        @Schema(description = "배너 리스트")
        List<BannerResponse> BannerResponseList;
    }

}
