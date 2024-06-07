package com.modutaxi.api.domain.report.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.ReportErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.report.dto.ReportRequestDto.ReportRequest;
import com.modutaxi.api.domain.report.dto.ReportResponseDto.ReportResponse;
import com.modutaxi.api.domain.report.service.RegisterReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reports")
@Tag(name = "신고", description = "신고하기 API")
public class RegisterReportController {

    private final RegisterReportService registerReportService;

    /**
     * [POST] 신고하기
     */
    @Operation(
        summary = "신고하기",
        description = "현재는 Member 신고만 가능합니다. targetId에 memberId를 넣어주세요. <br>" +
            "헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요! <br>" +
            "아래는 ReportType 입니다. ReportRequest 스케마에서도 확인 가능합니다!<br>" +
            "<ul> <li>LEAVE_CHATROOM(중간에 채팅방을 나갔어요)</li>" +
            "<li>LATE(제 시간에 도착하지 않았어요)</li>" +
            "<li>FIRST_GONE(먼저 출발했어요)</li>" +
            "<li>OUT_OF_TOUCH(연락이 되지 않아요)</li>" +
            "<li>UNEXPECTED_ACCOUNTS(정산 금액이 예상과 달라요)</li>" +
            "<li>NON_REMIT(정산 금액을 보내주지 않았어요)</li>" +
            "<li>NON_REMIT(기타 (직접 입력할게요))</li> </ul>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "신고 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportResponse.class))),
        @ApiResponse(responseCode = "400", description = "신고 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportErrorCode.class), examples = {
            @ExampleObject(name = "REPORT_001", description = "신고 사유가 10자 미만인 경우", value = """
                {
                    "errorCode": "REPORT_001",
                    "message": "신고 사유는 10자 이상 입력해주세요."
                }
                """),
        })),
        @ApiResponse(responseCode = "409", description = "신고 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReportErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_001", description = "존재하지 않는 멤버를 신고할 경우", value = """
                {
                    "errorCode": "MEMBER_001",
                    "message": "존재하지 않는 사용자입니다."
                }
                """),
            @ExampleObject(name = "REPORT_002", description = "자기 자신을 신고할 경우", value = """
                {
                    "errorCode": "REPORT_002",
                    "message": "자기 자신은 신고할 수 없습니다."
                }
                """),
        })),
    })
    @PostMapping("")
    public ResponseEntity<ReportResponse> registerReport(
        @CurrentMember Member member,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = ReportRequest.class)))
        @RequestBody ReportRequest request
    ) {
        return ResponseEntity.ok(registerReportService.register(
            member.getId(),
            request.getTargetId(),
            request.getRoomId(),
            request.getType(),
            request.getContent()));
    }
}
