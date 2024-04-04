package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import java.time.LocalDateTime;
import java.util.List;
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

        private List<RoomTagBitMask> roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalDateTime departTime;

        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {

        private Long destinationId;

        private String description;

        private List<RoomTagBitMask> roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalDateTime departTime;

        private int wishHeadcount;
    }
}
