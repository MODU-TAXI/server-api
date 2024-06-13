package com.modutaxi.api.domain.paymentmember.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberListResponse;
import com.modutaxi.api.domain.paymentmember.service.GetPaymentMemberService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "정산", description = "정산 멤버 조회 API")
@RequestMapping("/api/payment-members")
public class GetPaymentMemberController {

    private final GetPaymentMemberService getPaymentMemberService;

    /**
     * [GET] 정산 멤버 현황 조회
     */
    @Operation(
        summary = "정산 멤버 현황 조회",
        description = "정산 멤버 현황(프로필, 정산 유무 등)을 조회합니다.<br>" +
            "조회할 택시팟의 Id(roomId)를 보내주세요.<br>" +
            "정산멤버 status 종류: COMPLETE(정산 완료), INCOMPLETE(정산 미완료), DEACTIVATED(정산 대상 미포함)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정산 멤버 현황 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentMemberListResponse.class))),
        @ApiResponse(responseCode = "409", description = "정산 멤버 현황 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCode.class), examples = {
            @ExampleObject(name = "ROOM_001", description = "존재하지 않는 방입니다.", value = """
                {
                    "errorCode": "ROOM_001",
                    "message": "존재하지 않는 방입니다."
                }
                """),
            @ExampleObject(name = "PAYMENT_002", description = "존재하지 않는 정산방입니다.", value = """
                {
                    "errorCode": "PAYMENT_002",
                    "message": "존재하지 않는 정산방입니다."
                }
                """),
        })),
    })
    @GetMapping("")
    public ResponseEntity<PaymentMemberListResponse> getPaymentMembers(
        @CurrentMember Member member,
        @Parameter(description = "조회할 택시팟의 id") @RequestParam("roomId") Long roomId
    ) {
        return ResponseEntity.ok(getPaymentMemberService.getPaymentMembers(member, roomId));
    }
}
