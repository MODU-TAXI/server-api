package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

public class RoomRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateRoomRequest {
        @Schema(description = "도착 거점 id")
        private Long spotId;
        @Schema(description = "택시팟 카테고리")
        private List<RoomTagBitMask> roomTagBitMask;
        @Schema(example = "126.68557", description = "출발지 경도")
        private Float departureLongitude;
        @Schema(example = "37.46761", description = "출발지 위도")
        private Float departureLatitude;
        @Schema(description = "출발 시각")
        private LocalDateTime departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(description = "목표 인원수")
        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {
        @Schema(description = "도착 거점 id")
        private Long spotId;
        @Schema(description = "택시팟 카테고리")
        private List<RoomTagBitMask> roomTagBitMask;
        @Schema(example = "126.68557", description = "출발지 경도")
        private Float departureLongitude;
        @Schema(example = "37.46761", description = "출발지 위도")
        private Float departureLatitude;
        @Schema(description = "출발 시각")
        private LocalDateTime departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(description = "목표 인원수")
        private int wishHeadcount;
    }
}
