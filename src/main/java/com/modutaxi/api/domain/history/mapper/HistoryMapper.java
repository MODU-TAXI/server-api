package com.modutaxi.api.domain.history.mapper;

import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberListResponse;
import com.modutaxi.api.domain.room.entity.Room;
import org.springframework.stereotype.Component;

import java.util.List;

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

    public static HistoryMonthlyResponse toDto(int year, int month,
                                               int accumulateTotalCharge, int accumulatePortionCharge,
                                               List<HistorySimpleResponse> historySimpleResponseList) {
        return HistoryMonthlyResponse.builder()
            .year(year)
            .month(month)
            .accumulateTotalCharge(accumulateTotalCharge)
            .accumulatePortionCharge(accumulatePortionCharge)
            .historySimpleResponseList(historySimpleResponseList)
            .build();
    }

    public static HistoryDetailResponse toDto(
        History history, Room room, PaymentMemberListResponse paymentMemberListResponse) {
        return HistoryDetailResponse.builder()
            .managerId(room.getRoomManager().getId())
            .historyId(history.getId())
            .roomId(room.getId())
            .departureTime(room.getDepartureTime())
            .departureName(room.getDepartureName())
            .arrivalName(room.getSpot().getName())
            .totalCharge(history.getTotalCharge())
            .portionCharge(history.getPortionCharge())
            .paymentMemberListResponse(paymentMemberListResponse)
            .build();
    }

    public static HistorySimpleResponse toDto(History history) {
        return HistorySimpleResponse.builder()
            .historyId(history.getId())
            .departureTime(history.getRoom().getDepartureTime())
            .departureName(history.getRoom().getDepartureName())
            .arrivalName(history.getRoom().getSpot().getName())
            .portionCharge(history.getPortionCharge())
            .build();
    }


}
