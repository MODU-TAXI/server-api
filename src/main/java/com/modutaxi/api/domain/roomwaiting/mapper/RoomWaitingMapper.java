package com.modutaxi.api.domain.roomwaiting.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class RoomWaitingMapper {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RoomWaitingResponse {
        private Long memberId;
        private String nickname;
        private String imageUrl;
        private boolean thisIsMe;

        public static RoomWaitingResponse toDto(Member member, boolean thisIsMe) { //TODO Mapper 로 전환 요망
            return RoomWaitingResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .thisIsMe(thisIsMe)
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class RoomWaitingResponseList {
        @Schema(description = "대기열 리스트")
        private List<RoomWaitingResponse> waitingList;
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MemberRoomInResponse {
        private Long memberId;
        private String nickname;
        private String imageUrl;
        private boolean thisIsMe;

        public static MemberRoomInResponse toDto(Member member, boolean thisIsMe) {
            return MemberRoomInResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .thisIsMe(thisIsMe)
                .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class MemberRoomInResponseList {
        private List<MemberRoomInResponse> inList;
    }

    @Getter
    @AllArgsConstructor
    public static class ApplyResponse {
        private Boolean isApplied;
    }
}
