package com.modutaxi.api.domain.likedSpot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class LikedSpotResponseDto {
    @Getter
    @AllArgsConstructor
    public static class LikedSpotResponse {
        @Schema(example = "true", description = "수행 여부")
        private Boolean isOperated;
    }
}
