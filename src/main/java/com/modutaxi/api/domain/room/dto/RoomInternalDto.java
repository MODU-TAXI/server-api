package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.room.entity.Room;
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

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;

        private int expectedCharge;

        private long duration;

        public static InternalUpdateRoomDto toDto(Room room) {
            GeometryFactory geometryFactory = new GeometryFactory();
            Coordinate coordinate = new Coordinate(room.getDeparturePoint().getX(), room.getDeparturePoint().getY());
            Point point = geometryFactory.createPoint(coordinate);
            return InternalUpdateRoomDto.builder()
                .spot(room.getSpot())
                .roomTagBitMask(room.getRoomTagBitMask())
                .departurePoint(point)
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }

}
