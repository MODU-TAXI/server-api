package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.geo.Point;

public class RoomRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CreateRoomRequest {

        private Long spotId;

        private String description;

        private List<RoomTagBitMask> roomTagBitMask;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }

    @Getter
    @Builder
    public static class UpdateRoomRequest {

        private Long spotId;

        private String description;

        private List<RoomTagBitMask> roomTagBitMask;

        private Point departurePoint;

        private LocalDateTime departureTime;

        private int wishHeadcount;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetSimpleListRequest {
        @Schema(example = "1", description = "찾으려는 거점 id")
        private Long spotId;
        @Schema(example = "false", description = "마감 임박")
        private Boolean isImminent;
        @Schema(example = "[\"REGARDLESS_OF_GENDER\", \"STUDENT_CERTIFICATION\"]", description = "모집방 태그")
        private List<RoomTagBitMask> roomTags;
    }
}
