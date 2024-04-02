package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.taxiinfo.entity.Point;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomResponseDto {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomDetailResponse {
        private Long destinationId;

        private String description;

        private int roomTagBitMask;

        private float startLongitude;

        private float startLatitude;

        private LocalTime departTime;

        private int wishHeadcount;

        private long duration;

        private int expectedCharge;

        private List<Point> path;

        public static RoomDetailResponse toDto(Room room, List<Point>  path){
            return RoomDetailResponse.builder()
                .destinationId(room.getDestination().getId())
                .description(room.getDescription())
                .roomTagBitMask(room.getRoomTagBitMask())
                .startLatitude(room.getStartLatitude())
                .startLongitude(room.getStartLongitude())
                .departTime(room.getDepartTime())
                .wishHeadcount(room.getWishHeadcount())
                .duration(room.getDuration())
                .expectedCharge(room.getExpectedCharge())
                .path(path)
                .build();
        }
    }
}
