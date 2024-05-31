package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.mongodb.client.model.geojson.LineString;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomDetailResponse {
        @Schema(example = "1", description = "매니저 ID")
        private Long managerId;
        @Schema(example = "String", description = "프로필 이미지 url")
        private String profileImageUrl;
        @Schema(example = "true", description = "내가 생성한 방인 지 체크")
        private boolean isMyRoom;
        @Schema(example = "true", description = "내가 참여해 있는 방인 지 체크")
        private boolean isParticipate;
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(description = "도착 거점 id")
        private Long spotId;
        @Schema(example = "2022.05.05 (일)", description = "출발 일자")
        private String departureDairyDate;
        @Schema(example = "126.65464", description = "도착지 경도")
        private Float arrivalLongitude;
        @Schema(example = "37.45169", description = "도착지 위도")
        private Float arrivalLatitude;
        @Schema(example = "14:00", description = "도착 시간")
        private String arrivalTime;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(description = "택시팟 카테고리")
        private List<RoomTagBitMask> roomTagBitMaskList;
        @Schema(example = "126.68557", description = "출발지 경도")
        private Float departureLongitude;
        @Schema(example = "37.46761", description = "출발지 위도")
        private Float departureLatitude;
        @Schema(example = "12:00", description = "출발 시간")
        private String departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "2", description = "현재 인원수")
        private int currentHeadcount;
        @Schema(example = "3", description = "목표 인원수")
        private int wishHeadcount;
        @Schema(example = "30", description = "이동 예상시간 (분)")
        private long durationMinutes;
        @Schema(example = "5000", description = "인당 예상 요금(목표 인원 다 찼을 때 기준)")
        private int expectedChargePerPerson;
        @Schema(example = "15000", description = "예상 요금")
        private int expectedCharge;
        @Schema(description = "경로")
        private LineString path;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomSimpleResponse {
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(description = "도착 거점 id")
        private Long spotId;
        @Schema(example = "14:00", description = "도착 시간")
        private String arrivalTime;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(description = "택시팟 카테고리")
        private List<RoomTagBitMask> roomTagBitMaskList;
        @Schema(example = "12:00", description = "출발 시간")
        private String departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "2", description = "현재 인원수")
        private int currentHeadcount;
        @Schema(example = "3", description = "목표 인원수")
        private int wishHeadcount;
        @Schema(example = "30", description = "이동 예상시간 (분)")
        private long durationMinutes;
        @Schema(example = "5000", description = "인당 예상 요금(목표 인원 다 찼을 때 기준)")
        private int expectedChargePerPerson;
        @Schema(example = "15000", description = "예상 요금")
        private int expectedCharge;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchRoomWithRadiusResponse {
        @Schema(example = "2", description = "방 id")
        private Long id;
        @Schema(example = "126.65464", description = "출발지 경도")
        private Float departureLongitude;
        @Schema(example = "37.45169", description = "출발지 위도")
        private Float departureLatitude;
        @Schema(example = "주안역", description = "거점 이름")
        private String spotName;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchRoomWithRadiusResponses {
        @Schema(description = "방 리스트")
        List<SearchRoomWithRadiusResponse> rooms;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteRoomResponse {
        @Schema(example = "true", description = "수행완료 여부")
        private Boolean isDeleted;
    }
}
