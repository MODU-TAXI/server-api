package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.room.entity.Room;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;

public class RoomInternalDto {

    @Getter
    @Setter
    @Builder
    public static class InternalUpdateRoomDto {

        private Spot spot;

        private int roomTagBitMask;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;

        private int expectedCharge;

        private long duration;

        public static InternalUpdateRoomDto toDto(Room room) {
            return InternalUpdateRoomDto.builder()
                .spot(room.getSpot())
                .roomTagBitMask(room.getRoomTagBitMask())
                .departurePoint(room.getDeparturePoint())
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }

}
