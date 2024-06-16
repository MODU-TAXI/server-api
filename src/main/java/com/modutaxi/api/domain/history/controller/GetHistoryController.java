package com.modutaxi.api.domain.history.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.history.dto.HistoryResponseDto.HistoryDetailResponse;
import com.modutaxi.api.domain.history.service.HistoryService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/Histories")
@Tag(name = "이용 내역 조회", description = "이용 내역 조회 API")
public class GetHistoryController {

    private final HistoryService historyService;


    //상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<HistoryDetailResponse> getHistoryDetail(@CurrentMember Member member, @PathVariable Long id) {
        return ResponseEntity.ok(historyService.getHistoryDetail(member, id));
    }

}
