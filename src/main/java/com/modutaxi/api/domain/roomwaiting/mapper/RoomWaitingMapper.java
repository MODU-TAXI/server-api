package com.modutaxi.api.domain.roomwaiting.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.roomwaiting.entity.RoomWaiting;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomWaitingMapper {

    public static RoomWaiting toEntity(Member member, Room room) {
        return RoomWaiting.builder()
                .member(member)
                .room(room)
                .build();
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class RoomWaitingResponse {
        private Long memberId;
        private String nickname;
        private String imageUrl;
        private boolean isCertified;
        private int matchingCount;
        private boolean thisIsMe;

        public static RoomWaitingResponse toDto(Member member,
            boolean thisIsMe) { //TODO Mapper 로 전환 요망
            return RoomWaitingResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .isCertified(member.isCertified())
                .imageUrl(member.getImageUrl())
                .matchingCount(member.getMatchingCount())
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
        private boolean isCertified;
        private int matchingCount;
        private boolean thisIsMe;

        public static MemberRoomInResponse toDto(Member member, boolean thisIsMe) {
            return MemberRoomInResponse.builder()
                .memberId(member.getId())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .isCertified(member.isCertified())
                .matchingCount(member.getMatchingCount())
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
