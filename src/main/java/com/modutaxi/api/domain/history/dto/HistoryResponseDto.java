package com.modutaxi.api.domain.history.dto;

import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class HistoryResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class HistoryDetailResponse {
        @Schema(description = "이용 기록 id")
        private Long historyId;
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(example = "12:00", description = "출발 시간")
        private LocalDateTime departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(example = "14451", description = "총액")
        private int totalCharge;
        @Schema(example = "2613", description = "최종 정산 금액(인당 요금)")
        private int portionCharge;
        @Schema(description = "참여 멤버 리스트")
        private MemberRoomInResponseList participantList;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class HistorySimpleResponse {
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(example = "12:00", description = "출발 시간")
        private String departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(example = "2", description = "현재 인원수")
        private int currentHeadcount;
        @Schema(example = "2613", description = "최종 정산 금액(인당 요금)")
        private int portionCharge;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class HistoryMonthlyResponse {
        @Schema(description = "택시팟 id")
        private Long roomId;
        @Schema(example = "12:00", description = "출발 시간")
        private String departureTime;
        @Schema(example = "센트리빌", description = "출발지 이름")
        private String departureName;
        @Schema(example = "주안역", description = "도착 거점 이름")
        private String arrivalName;
        @Schema(example = "2613", description = "최종 정산 금액(인당 요금)")
        private int portionCharge;
    }

}
