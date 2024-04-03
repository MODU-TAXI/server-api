package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.room.entity.Room;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class RoomInternalDto {

    @Getter
    @Setter
    @Builder
    public static class InternalUpdateRoomDto {

        private Destination destination;

        private String description;

        private int roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalDateTime departTime;

        private int wishHeadcount;

        private int expectedCharge;

        private long duration;

        public static InternalUpdateRoomDto toDto(Room room) {
            return InternalUpdateRoomDto.builder()
                .destination(room.getDestination())
                .description(room.getDescription())
                .roomTagBitMask(room.getRoomTagBitMask())
                .startLatitude(room.getStartLatitude())
                .startLongitude(room.getStartLongitude())
                .departTime(room.getDepartTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }

}
