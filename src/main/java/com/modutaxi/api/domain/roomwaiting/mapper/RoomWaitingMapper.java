package com.modutaxi.api.domain.roomwaiting.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.roomwaiting.dto.RoomWaitingResponseDto.RoomWaitingResponse;
import com.modutaxi.api.domain.roomwaiting.entity.RoomWaiting;

public class RoomWaitingMapper {

    public static RoomWaiting toEntity(Member member, Room room) {
        return RoomWaiting.builder()
            .member(member)
            .room(room)
            .build();
    }

    public static RoomWaitingResponse toDto(Member member,
        boolean thisIsMe) {
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
