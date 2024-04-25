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

        private Long spotId;

        private List<RoomTagBitMask> roomTagBitMask;

        @Schema(example = "126.65464", description = "출발지 경도")
        private Float departureLongitude;

        @Schema(example = "37.45169", description = "출발지 위도")
        private Float departureLatitude;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {

        private Long spotId;

        private List<RoomTagBitMask> roomTagBitMask;

        @Schema(example = "126.65464", description = "출발지 경도")
        private Float departureLongitude;

        @Schema(example = "37.45169", description = "출발지 위도")
        private Float departureLatitude;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }
}
