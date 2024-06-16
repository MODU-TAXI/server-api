package com.modutaxi.api.domain.history.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.service.HistoryService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Histories")
@Tag(name = "이용 내역 조회", description = "이용 내역 조회 API")
public class GetHistoryController {

    private final HistoryService historyService;


    //상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<HistoryDetailResponse> getHistoryDetail(@CurrentMember Member member, @PathVariable Long id) {
        return ResponseEntity.ok(historyService.getDetailHistory(member, id));
    }

    //월별 Simple List & 누적금액 조회
    @GetMapping("/monthly")
    public ResponseEntity<HistoryMonthlyResponse> getMonthlyHistory(
        @CurrentMember Member member,
        @RequestParam int year,
        @RequestParam int month
    ) {
        return ResponseEntity.ok(historyService.getMonthlyHistory(member, year, month));
    }

    //전체 조회
    @GetMapping
    public ResponseEntity<List<HistorySimpleResponse>> getHistory(@CurrentMember Member member) {
        return ResponseEntity.ok(historyService.getSimpleHistoryList(member));
    }

}
