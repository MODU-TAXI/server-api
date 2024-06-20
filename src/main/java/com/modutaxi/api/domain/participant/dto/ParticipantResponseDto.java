package com.modutaxi.api.domain.participant.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ParticipantResponseDto {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class MemberRoomInResponse {

        private Long memberId;
        private String nickname;
        private String imageUrl;
        private boolean isCertified;
        private int matchingCount;
        private boolean thisIsMe;
    }

    @Getter
    @AllArgsConstructor
    public static class MemberRoomInResponseList {
        private List<MemberRoomInResponse> inList;
    }
}
