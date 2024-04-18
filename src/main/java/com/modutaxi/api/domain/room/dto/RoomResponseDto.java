package com.modutaxi.api.domain.room.dto;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertBitMaskToRoomTagList;

import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.mongodb.client.model.geojson.LineString;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.geo.Point;

public class RoomResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomDetailResponse {

        private Long roomId;

        private Long spotId;

        private List<RoomTagBitMask> roomTagBitMaskList;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;

        private long duration;

        private int expectedCharge;

        private LineString path;

        public static RoomDetailResponse toDto(Room room, LineString path) {
            return RoomDetailResponse.builder()
                .roomId(room.getId())
                .spotId(room.getSpot().getId())
                .roomTagBitMaskList(convertBitMaskToRoomTagList(room.getRoomTagBitMask()))
                .departurePoint(room.getDeparturePoint())
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .path(path)
                .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomSimpleResponse {

        private Long roomId;

        private Long spotId;

        private List<RoomTagBitMask> roomTagBitMaskList;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;

        private long duration;

        private int expectedCharge;

        public static RoomSimpleResponse toDto(Room room) {
            return RoomSimpleResponse.builder()
                .roomId(room.getId())
                .spotId(room.getSpot().getId())
                .roomTagBitMaskList(convertBitMaskToRoomTagList(room.getRoomTagBitMask()))
                .departurePoint(room.getDeparturePoint())
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }
}
