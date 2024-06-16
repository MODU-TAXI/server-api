package com.modutaxi.api.domain.history.mapper;

import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.*;
import org.springframework.stereotype.Component;

@Component
public class HistoryMapper {
    public static History toEntity(
        Room room, Member member, int totalCharge, int portionCharge) {
        return History.builder()
            .room(room)
            .member(member)
            .totalCharge(totalCharge)
            .portionCharge(portionCharge)
            .build();
    }

    public static HistoryDetailResponse toDto(
        History history, MemberRoomInResponseList participantList) {
        return HistoryDetailResponse.builder()
            .historyId(history.getId())
            .roomId(history.getRoom().getId())
            .departureTime(history.getRoom().getDepartureTime())
            .departureName(history.getRoom().getDepartureName())
            .arrivalName(history.getRoom().getSpot().getName())
            .totalCharge(history.getTotalCharge())
            .portionCharge(history.getPortionCharge())
            .participantList(participantList)
            .build();
    }

}
