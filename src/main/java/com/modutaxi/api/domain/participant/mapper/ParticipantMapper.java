package com.modutaxi.api.domain.participant.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.participant.dto.ParticipantResponseDto.MemberRoomInResponse;
import com.modutaxi.api.domain.participant.entity.Participant;
import com.modutaxi.api.domain.room.entity.Room;

public class ParticipantMapper {

    public static Participant toEntity(Member member, Room room) {
        return Participant.builder()
            .member(member)
            .room(room)
            .build();
    }

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
