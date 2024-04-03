package com.modutaxi.api.domain.room.mapper;

import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public static Room toEntity(
        Member member, String roomName, Destination destination, int expectedCharge, long duration,
        String description, int roomTagBitMask,
        float startLongitude, float startLatitude, LocalDateTime departTime
    ) {
        return Room.builder()
            .destination(destination)
            .roomManager(member)
            .expectedCharge(expectedCharge)
            .duration(duration)
            .roomName(roomName)
            .description(description)
            .roomTagBitMask(roomTagBitMask)
            .startLatitude(startLatitude)
            .startLongitude(startLongitude)
            .departTime(departTime)
            .build();
    }
}
