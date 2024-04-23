package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.room.entity.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

public class RoomInternalDto {

    @Getter
    @Setter
    @Builder
    public static class InternalUpdateRoomDto {

        private Spot spot;

        private int roomTagBitMask;

        private Float longitude;

        private Float latitude;

        private LocalDateTime departureTime;

        private int wishHeadcount;

        private int expectedCharge;

        private long duration;

        public static InternalUpdateRoomDto toDto(Room room) {
            return InternalUpdateRoomDto.builder()
                .spot(room.getSpot())
                .roomTagBitMask(room.getRoomTagBitMask())
                .longitude((float) room.getDeparturePoint().getX())
                .latitude((float) room.getDeparturePoint().getY())
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }

}
