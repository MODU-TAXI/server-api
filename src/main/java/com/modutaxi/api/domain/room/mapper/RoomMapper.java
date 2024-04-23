package com.modutaxi.api.domain.room.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import java.time.LocalDateTime;

import com.modutaxi.api.domain.spot.entity.Spot;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public static Room toEntity(
        Member member,
        Spot spot,
        int expectedCharge,
        long duration,
        int roomTagBitMask,
        float departureLongitude,
        float departureLatitude,
        LocalDateTime departureTime
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(departureLongitude, departureLatitude);
        Point point = geometryFactory.createPoint(coordinate);
        return Room.builder()
            .spot(spot)
            .roomManager(member)
            .expectedCharge(expectedCharge)
            .duration(duration)
            .roomTagBitMask(roomTagBitMask)
            .departureTime(departureTime)
            .departurePoint(point)
            .build();
    }
}
