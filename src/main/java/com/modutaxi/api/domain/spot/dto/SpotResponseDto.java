package com.modutaxi.api.domain.spot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.geo.Point;

public class SpotResponseDto {
    @Getter
    @AllArgsConstructor
    public static class CreateSpotResponse {
        @Schema(example = "1", description = "거점 id")
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteSpotResponse {
        @Schema(example = "1", description = "거점 id")
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateSpotResponse {
        @Schema(example = "1", description = "거점 id")
        private Long id;
    }

    @Getter
    @AllArgsConstructor
    public static class GetSpotWithDistanceResponse {
        @Schema(example = "2", description = "거점 id")
        private Long id;
        @Schema(example = "주안역", description = "거점 이름")
        private String name;
        @Schema(example = "인천 미추홀구 주안로 95-19", description = "거점 주소")
        private String address;
        @Schema(example = "{\"x\": 126.68045, \"y\": 37.46504}", description = "거점 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
        @Schema(example = "3204.821669916938", description = "거리, 미터단위")
        private Double distance;
    }
}
