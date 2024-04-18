package com.modutaxi.api.domain.spot.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.geo.Point;

import java.util.List;

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
        @Schema(example = "true", description = "좋아요 여부")
        private Boolean liked;
    }

    @Getter
    @AllArgsConstructor
    public static class GetSpotWithDistanceResponses {
        @Schema(description = "거점 리스트")
        List<GetSpotWithDistanceResponse> spots;
    }

    @Getter
    @AllArgsConstructor
    public static class GetSpotResponse {
        @Schema(example = "1", description = "거점 id")
        private Long id;
        @Schema(example = "인하대학교 후문", description = "거점 이름")
        private String name;
        @Schema(example = "인천 미추홀구 용현동 253", description = "거점 주소")
        private String address;
        @Schema(example = "{\"x\": 126.65464, \"y\": 37.45169}", description = "거점 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
    }

    @Getter
    @AllArgsConstructor
    public static class GetSpotResponses {
        @Schema(description = "거점 리스트")
        List<GetSpotResponse> spots;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchWithRadiusResponse {
        @Schema(example = "2", description = "거점 id")
        private Long id;
        @Schema(example = "{\"x\": 126.68045, \"y\": 37.46504}", description = "검색 위치<br>x: 경도, y: 위도")
        private Point spotPoint;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchWithRadiusResponses {
        @Schema(description = "거점 리스트")
        List<SearchWithRadiusResponse> spots;
    }
}