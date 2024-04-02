package com.modutaxi.api.domain.room.dto;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateRoomRequest {
        private Long destinationId;

        private String description;

        private String roomName;

        private int roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalTime departTime;

        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {
        private Long destinationId;

        private String description;

        private int roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalTime departTime;

        private int wishHeadcount;
    }
}
