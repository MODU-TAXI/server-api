package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
}
