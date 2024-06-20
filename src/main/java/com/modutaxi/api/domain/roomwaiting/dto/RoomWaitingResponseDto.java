package com.modutaxi.api.domain.roomwaiting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomWaitingResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RoomWaitingResponse {
        private Long memberId;
        private String nickname;
        private String imageUrl;
        private boolean isCertified;
        private int matchingCount;
        private boolean thisIsMe;
    }

    @Getter
    @AllArgsConstructor
    public static class RoomWaitingResponseList {
        @Schema(description = "대기열 리스트")
        private List<RoomWaitingResponse> waitingList;
    }

    @Getter
    @AllArgsConstructor
    public static class ApplyResponse {
        private Boolean isApplied;
    }
}
