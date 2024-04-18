package com.modutaxi.api.domain.likedSpot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.geo.Point;

public class LikedSpotResponseDto {
    @Getter
    @AllArgsConstructor
    public static class LikedSpotResponse {
        @Schema(example = "true", description = "수행 여부")
        private Boolean isOperated;
    }
    @Getter
    @AllArgsConstructor
    public static class LikedSpotListResponse {
        @Schema(example = "2", description = "거점 id")
        private Long spotId;
        @Schema(example = "주안역", description = "거점 이름")
        private String spotName;
        @Schema(example = "{\"x\": 126.68045, \"y\": 37.46504}", description = "거점 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
    }
}
