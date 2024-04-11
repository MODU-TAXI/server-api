package com.modutaxi.api.domain.spot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class SpotResponseDto {
    @Getter
    @AllArgsConstructor
    public static class CreateSpotResponse {
        @Schema(example = "1", description = "거점 id")
        private Long id;
    }
}