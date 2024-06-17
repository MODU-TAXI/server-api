package com.modutaxi.api.domain.history.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.HistoryErrorCode;
import com.modutaxi.api.common.exception.errorcode.PaymentErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.history.dto.HistoryResponseDto.*;
import com.modutaxi.api.domain.history.service.HistoryService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
@RequestMapping("/api/histories")
@Tag(name = "이용 내역 조회", description = "이용 내역 조회 API")
public class GetHistoryController {

    private final HistoryService historyService;


    /**
     * [GET] 이용 내역 상세 조회
     */
    @Operation(
        summary = "이용 내역 상세 조회",
        description = "이용 내역 상세(managerId, roomId, 출발시간, 출발지, 정산요금, 참가자 리스트 등을 조회합니다.<br>" +
            "조회할 이용 내역 id(historyId)를 보내주세요.<br>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "이용 내역 상세 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "이용 내역 상세 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryErrorCode.class), examples = {
            @ExampleObject(name = "HISTORY_001", value = """
                {
                    "code": "HISTORY_001",
                    "message": "존재하지 않는 이용 내역입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "일치하는 ID를 가진 이용 내역이 존재하지 않습니다.")
        })),
        @ApiResponse(responseCode = "400", description = "이용 내역 상세 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomErrorCode.class), examples = {
            @ExampleObject(name = "ROOM_001", value = """
                {
                    "code": "ROOM_001",
                    "message": "존재하지 않는 방입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "존재하지 않는 방(택시팟)입니다.")
        })),
        @ApiResponse(responseCode = "409", description = "이용 내역 상세 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentErrorCode.class), examples = {
            @ExampleObject(name = "PAYMENT_002", value = """
                {
                    "code": "PAYMENT_002",
                    "message": "존재하지 않는 정산방입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "존재하지 않는 정산방입니다.")
        })),
    })
    @GetMapping("/{id}")
    public ResponseEntity<HistoryDetailResponse> getHistoryDetail(@CurrentMember Member member, @PathVariable Long id) {
        return ResponseEntity.ok(historyService.getDetailHistory(member, id));
    }

    /**
     * [GET] 월별 Simple List & 누적금액 조회
     */
    @Operation(
        summary = "월 별 이용내역 조회 성공",
        description = "해당 월에 해당되는 기록들의 간단한 정보 리스트와 년,월, 월별 누적 정산 금액, 누적 결제 금액이 반환됩니다.(월별 Simple List & 누적금액 조회) <br>**필수 파라미터** 조회하고자 하는 년, 월<br>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "월 별 이용내역 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistoryMonthlyResponse.class))),
    })
    @GetMapping("/monthly")
    public ResponseEntity<HistoryMonthlyResponse> getMonthlyHistory(
        @CurrentMember Member member,
        @Parameter(description = "검색하고 싶은 년") @RequestParam(value = "year") int year,
        @Parameter(description = "검색하고 싶은 월") @RequestParam(value = "month") int month
    ) {
        return ResponseEntity.ok(historyService.getMonthlyHistory(member, year, month));
    }

    /**
     * [GET] 내 이용 내역 전체 조회
     */
    @Operation(
        summary = "내 이용 내역 전체 조회",
        description = "내 이용내역을 List형태로 반환합니다.<br>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "내 이용 내역 전체 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HistorySimpleListResponse.class))),
    })
    @GetMapping
    public ResponseEntity<HistorySimpleListResponse> getHistory(@CurrentMember Member member) {
        return ResponseEntity.ok(historyService.getSimpleHistoryList(member));
    }

}
