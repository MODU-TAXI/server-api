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

        private String description;

        private List<RoomTagBitMask> roomTagBitMask;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {

        private Long spotId;

        private String description;

        private List<RoomTagBitMask> roomTagBitMask;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchRoomPointRequest {
        @Schema(example = "{\"x\": 126.65157, \"y\": 37.44747}", description = "검색 위치<br>x: 경도, y: 위도")
        private Point searchPoint;
    }
}
