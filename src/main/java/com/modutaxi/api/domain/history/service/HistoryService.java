package com.modutaxi.api.domain.history.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.HistoryErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.entity.History;
import com.modutaxi.api.domain.history.mapper.HistoryMapper;
import com.modutaxi.api.domain.history.repository.HistoryRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberListResponse;
import com.modutaxi.api.domain.paymentmember.service.GetPaymentMemberService;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import jakarta.persistence.Tuple;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class HistoryService {
    private final HistoryRepository historyRepository;
    private final RoomRepository roomRepository;
    private final GetPaymentMemberService getPaymentMemberService;


    public HistoryDetailResponse getDetailHistory(Member member, Long historyId) {

        History history = historyRepository.findById(historyId)
            .orElseThrow(() -> new BaseException(HistoryErrorCode.EMPTY_HISTORY));

        Room room = roomRepository.findById(history.getRoom().getId())
            .orElseThrow(() -> new BaseException(RoomErrorCode.EMPTY_ROOM));


        PaymentMemberListResponse paymentMemberList = getPaymentMemberService.getPaymentMembers(member, room.getId());

        return HistoryMapper.toDto(history, room, paymentMemberList);
    }


    public HistoryMonthlyResponse getMonthlyHistory(Member member, int year, int month) {
        Tuple monthlyCharge =
            historyRepository.findTotalChargeAndPortionChargeByMemberIdAndDepartureDate(member.getId(), year, month);

        // Long 타입으로 가져온 후 null 체크하고, int로 변환
        Long totalChargeLong = (Long) monthlyCharge.get("accumulateTotalCharge");
        Long portionChargeLong = (Long) monthlyCharge.get("accumulatePortionCharge");

        int accumulateTotalCharge = (totalChargeLong != null) ? totalChargeLong.intValue() : 0;
        int accumulatePortionCharge = (portionChargeLong != null) ? portionChargeLong.intValue() : 0;

        List<History> historyList = historyRepository.findByMemberIdAndDepartureDate(member.getId(), year, month);

        List<HistorySimpleResponse> historySimpleListResponse = historyList.stream()
            .map(HistoryMapper::toDto)
            .toList();

        return HistoryMapper.toDto(year, month, accumulateTotalCharge, accumulatePortionCharge, historySimpleListResponse);
    }

    public HistorySimpleListResponse getSimpleHistoryList(Member member) {
        List<History> historyList = historyRepository.findAllByMemberOrderByRoomDepartureTimeDesc(member);

        return HistoryMapper.toDto(historyList.stream()
            .map(HistoryMapper::toDto)
            .toList());
    }

    public HistoryDurationResponse getHistoryDuration(Member member) {
        Tuple tuple = historyRepository.findStartDateAndEndDateByMemberId(member.getId());
        LocalDateTime startDate = (LocalDateTime) tuple.get("startDate");
        LocalDateTime endDate = (LocalDateTime) tuple.get("endDate");
        return HistoryMapper.toDto(startDate, endDate);
    }


}
