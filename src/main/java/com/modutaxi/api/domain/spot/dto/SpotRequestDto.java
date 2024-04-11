package com.modutaxi.api.domain.spot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

public class SpotRequestDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateSpotRequest {
        @Schema(example = "인하대학교 후문", description = "거점 이름")
        private String name;
        @Schema(example = "인천광역시 미추홀구 인하로 100", description = "거점 주소")
        private String address;
        @Schema(example = "{\"x\": 126.65464, \"y\": 37.45169}", description = "거점 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateSpotRequest {
        @Schema(example = "인하대학교 후문", description = "거점 이름")
        private String name;
        @Schema(example = "인천광역시 미추홀구 인하로 100", description = "거점 주소")
        private String address;
        @Schema(example = "{\"x\": 126.65464, \"y\": 37.45169}", description = "거점 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
    }
}
