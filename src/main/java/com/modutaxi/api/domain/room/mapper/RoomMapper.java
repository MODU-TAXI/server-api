package com.modutaxi.api.domain.room.mapper;

import com.modutaxi.api.common.util.time.TimeFormatConverter;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomSimpleResponse;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.mongodb.client.model.geojson.LineString;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertBitMaskToRoomTagList;

@Component
public class RoomMapper {

    public static Room toEntity(
        Member member,
        Spot spot,
        int expectedCharge,
        long durationMinutes,
        int roomTagBitMask,
        float departureLongitude,
        float departureLatitude,
        LocalDateTime departureTime,
        String departureName,
        int wishHeadcount
    ) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(departureLongitude, departureLatitude);
        Point point = geometryFactory.createPoint(coordinate);
        return Room.builder()
            .spot(spot)
            .roomManager(member)
            .expectedCharge(expectedCharge)
            .durationMinutes(durationMinutes)
            .roomTagBitMask(roomTagBitMask)
            .departureTime(departureTime)
            .departurePoint(point)
            .departureName(departureName)
            .wishHeadcount(wishHeadcount)
            .build();
    }

    public static RoomDetailResponse toDto(Room room, Member member, LineString path) {
        return RoomDetailResponse.builder()
            .managerId(member.getId())
            .score(member.getScore())
            .isMyRoom(room.getRoomManager().getId().equals(member.getId()))
            .roomId(room.getId())
            .spotId(room.getSpot().getId())
            .departureDairyDate(TimeFormatConverter.convertTimeToDiaryDate(room.getDepartureTime()))
            .arrivalLongitude((float) room.getSpot().getSpotPoint().getX())
            .arrivalLatitude((float) room.getSpot().getSpotPoint().getY())
            .arrivalTime(TimeFormatConverter.covertTimeToShortClockTime(room.getDepartureTime().plusMinutes(room.getDurationMinutes())))
            .arrivalName(room.getSpot().getName())
            .roomTagBitMaskList(convertBitMaskToRoomTagList(room.getRoomTagBitMask()))
            .departureLongitude((float) room.getDeparturePoint().getX())
            .departureLatitude((float) room.getDeparturePoint().getY())
            .departureTime(TimeFormatConverter.covertTimeToShortClockTime(room.getDepartureTime()))
            .departureName(room.getDepartureName())
            .currentHeadcount(room.getCurrentHeadcount())
            .wishHeadcount(room.getWishHeadcount())
            .durationMinutes(room.getDurationMinutes())
            .expectedChargePerPerson((room.getExpectedCharge()) / (room.getWishHeadcount() + 1))
            .expectedCharge(room.getExpectedCharge())
            .path(path)
            .build();
    }

    public static RoomSimpleResponse toDto(Room room) {
        return RoomSimpleResponse.builder()
            .roomId(room.getId())
            .spotId(room.getSpot().getId())
            .arrivalTime(TimeFormatConverter.covertTimeToShortClockTime(room.getDepartureTime().plusMinutes(room.getDurationMinutes())))
            .arrivalName(room.getSpot().getName())
            .roomTagBitMaskList(convertBitMaskToRoomTagList(room.getRoomTagBitMask()))
            .departureTime(TimeFormatConverter.covertTimeToShortClockTime(room.getDepartureTime()))
            .departureName(room.getDepartureName())
            .currentHeadcount(room.getCurrentHeadcount())
            .wishHeadcount(room.getWishHeadcount())
            .durationMinutes(room.getDurationMinutes())
            .expectedChargePerPerson((room.getExpectedCharge()) / (room.getWishHeadcount() + 1))
            .expectedCharge(room.getExpectedCharge())
            .build();
    }
}
