package com.modutaxi.api.domain.alarm.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.alarm.dto.AlarmResponseDto.AlarmInfo;
import com.modutaxi.api.domain.alarm.service.GetAlarmService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms")
@Tag(name = "알림")
public class GetAlarmController {

    private final GetAlarmService getAlarmService;

    @Operation(summary = "알림 목록 조회", description = "내 알림 목록을 조회합니다.<br>page와 size를 입력해주세요!")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AlarmInfo.class))),
    })
    @GetMapping("")
    public ResponseEntity<PageResponseDto<List<AlarmInfo>>> getAlarmList(
        @CurrentMember Member member,
        @Parameter(description = "조회할 page") @RequestParam int page,
        @Parameter(description = "조회할 page 단위") @RequestParam int size) {
        return ResponseEntity.ok(getAlarmService.getAlarmList(member, page, size));
    }
}
