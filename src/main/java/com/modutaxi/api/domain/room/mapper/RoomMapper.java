package com.modutaxi.api.domain.room.mapper;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertBitMaskToRoomTagList;

import com.modutaxi.api.common.util.time.TimeFormatConverter;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchIntegrationResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchListResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchMapResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.*;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.mongodb.client.model.geojson.LineString;
import java.time.LocalDateTime;
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

    public static RoomDetailResponse toDto(
        Room room, Member member, LineString path, boolean isParticipate, boolean isWaiting) {
        return RoomDetailResponse.builder()
            .managerId(room.getRoomManager().getId())
            .isMyRoom(room.getRoomManager().getId().equals(member.getId()))
            .isParticipate(isParticipate)
            .isWaiting(isWaiting)
            .roomId(room.getId())
            .spotId(room.getSpot().getId())
            .departureDairyDate(TimeFormatConverter.convertTimeToDiaryDate(room.getDepartureTime()))
            .arrivalLongitude((float) room.getSpot().getSpotPoint().getX())
            .arrivalLatitude((float) room.getSpot().getSpotPoint().getY())
            .arrivalTime(TimeFormatConverter.covertTimeToShortClockTime(
                room.getDepartureTime().plusMinutes(room.getDurationMinutes())))
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

    public static SearchRoomIntegrationResponse toDto(SearchIntegrationResponse dao) {
        return SearchRoomIntegrationResponse.builder()
            .roomId(dao.getId())
            .spotId(dao.getSpotId())
            .arrivalTime(TimeFormatConverter.covertTimeToShortClockTime(
                dao.getDepartureTime().plusMinutes(dao.getDurationMinutes())))
            .arrivalName(dao.getSpotName())
            .departureLongitude((float) dao.getDeparturePoint().getX())
            .departureLatitude((float) dao.getDeparturePoint().getY())
            .roomTagBitMaskList(convertBitMaskToRoomTagList(dao.getRoomTagBitMask()))
            .departureTime(TimeFormatConverter.covertTimeToShortClockTime(dao.getDepartureTime()))
            .departureName(dao.getDepartureName())
            .currentHeadcount(dao.getCurrentHeadcount())
            .wishHeadcount(dao.getWishHeadcount())
            .durationMinutes(dao.getDurationMinutes())
            .expectedChargePerPerson((dao.getExpectedCharge()) / (dao.getWishHeadcount() + 1))
            .expectedCharge(dao.getExpectedCharge())
            .build();
    }

    public static RoomSimpleResponse toDto(SearchListResponse dao) {
        return RoomSimpleResponse.builder()
            .roomId(dao.getId())
            .spotId(dao.getSpotId())
            .arrivalTime(TimeFormatConverter.covertTimeToShortClockTime(
                dao.getDepartureTime().plusMinutes(dao.getDurationMinutes())))
            .arrivalName(dao.getSpotName())
            .roomTagBitMaskList(convertBitMaskToRoomTagList(dao.getRoomTagBitMask()))
            .departureTime(TimeFormatConverter.covertTimeToShortClockTime(dao.getDepartureTime()))
            .departureName(dao.getDepartureName())
            .currentHeadcount(dao.getCurrentHeadcount())
            .wishHeadcount(dao.getWishHeadcount())
            .durationMinutes(dao.getDurationMinutes())
            .expectedChargePerPerson((dao.getExpectedCharge()) / (dao.getWishHeadcount() + 1))
            .expectedCharge(dao.getExpectedCharge())
            .build();
    }

    public static SearchRoomWithRadiusResponse toDto(SearchMapResponse dao) {
        return new SearchRoomWithRadiusResponse(dao.getId(), (float) dao.getDeparturePoint().getX(), (float) dao.getDeparturePoint().getY(), dao.getSpotName());
    }

    public static RoomPreviewResponse toDto(Room room) {
        return RoomPreviewResponse.builder()
            .roomId(room.getId())
            .arrivalName(room.getSpot().getName())
            .departureTime(TimeFormatConverter.covertTimeToShortClockTime(room.getDepartureTime()))
            .departureName(room.getDepartureName())
            .currentHeadcount(room.getCurrentHeadcount())
            .wishHeadcount(room.getWishHeadcount()+1)
            .roomStatus(room.getRoomStatus())
            .expectedChargePerPerson((room.getExpectedCharge()) / (room.getWishHeadcount() + 1))
            .expectedCharge(room.getExpectedCharge())
            .build();
    }
}
