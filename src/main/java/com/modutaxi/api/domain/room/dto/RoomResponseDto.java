package com.modutaxi.api.domain.room.dto;

import static com.modutaxi.api.common.converter.RoomTagBitMaskConverter.convertBitMaskToRoomTagList;

import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.mongodb.client.model.geojson.LineString;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
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
                .departurePoint(new Point(room.getDeparturePoint().getX(), room.getDeparturePoint().getY()))
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
                .departurePoint(new Point(room.getDeparturePoint().getX(), room.getDeparturePoint().getY()))
                .departureTime(room.getDepartureTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class SearchWithRadiusResponse {
        @Schema(example = "2", description = "방 id")
        private Long id;
        @Schema(example = "126.68045", description = "경도")
        private Float longitude;
        @Schema(example = "37.46504", description = "위도")
        private Float latitude;
        @Schema(example = "주안역", description = "거점 이름")
        private String spotName;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchWithRadiusResponses {
        @Schema(description = "방 리스트")
        List<SearchWithRadiusResponse> rooms;
    }
}
