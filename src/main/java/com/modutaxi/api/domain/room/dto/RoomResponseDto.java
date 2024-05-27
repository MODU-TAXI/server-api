package com.modutaxi.api.domain.room.dto;

import com.modutaxi.api.domain.room.entity.RoomStatus;
import com.modutaxi.api.domain.room.entity.RoomTagBitMask;
import com.mongodb.client.model.geojson.LineString;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RoomResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomDetailResponse {
        @Schema(example = "1", description = "매니저 ID")
        private Long managerId;
        @Schema(example = "String", description = "프로필 이미지 url")
        private String profileImageUrl;
        @Schema(example = "0", description = "점수")
        private double score;
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
    @Builder
    @AllArgsConstructor
    public static class RoomPreviewResponse {
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(example = "12:00", description = "출발 시간")
        private String departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(example = "모집 중", description = "현재 방 상태")
        private RoomStatus roomStatus;
        @Schema(example = "2", description = "현재 인원수")
        private int currentHeadcount;
        @Schema(example = "3", description = "목표 인원수")
        private int wishHeadcount;
        @Schema(example = "5000", description = "인당 예상 요금(목표 인원 다 찼을 때 기준)")
        private int expectedChargePerPerson;
        @Schema(example = "15000", description = "예상 요금")
        private int expectedCharge;
    }

    @Getter
    @AllArgsConstructor
    public static class SearchWithRadiusResponse {
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
    public static class SearchWithRadiusResponses {
        @Schema(description = "방 리스트")
        List<SearchWithRadiusResponse> rooms;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteRoomResponse {
        @Schema(example = "true", description = "수행완료 여부")
        private Boolean isDeleted;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateRoomResponse {
        @Schema(example = "true", description = "수행완료 여부")
        private Boolean isUpdated;
    }
}
