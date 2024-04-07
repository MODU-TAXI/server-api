package com.modutaxi.api.domain.room.mapper;

import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import java.time.LocalDateTime;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public static Room toEntity(
        Member member, Destination destination, int expectedCharge, long duration,
        int roomTagBitMask, Point departurePoint, LocalDateTime departureTime
    ) {
        return Room.builder()
            .destination(destination)
            .roomManager(member)
            .expectedCharge(expectedCharge)
            .duration(duration)
            .roomTagBitMask(roomTagBitMask)
            .departureTime(departureTime)
            .departurePoint(departurePoint)
            .build();
    }
}
