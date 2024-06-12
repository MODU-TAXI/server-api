package com.modutaxi.api.domain.paymentmember.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.UpdatePaymentMemberResponse;
import com.modutaxi.api.domain.paymentmember.service.UpdatePaymentMemberService;
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
@Tag(name = "정산", description = "정산 완료 API")
@RequestMapping("/api/payment-members")
public class UpdatePaymentMemberController {

    private final UpdatePaymentMemberService updatePaymentMemberService;

    /**
     * [PATCH] 정산 완료
     */
    @Operation(
        summary = "정산 완료",
        description = "멤버의 정산 완료 API 입니다.<br>" +
            "택시팟의 Id(roomId)를 보내주세요.<br>" +
            "반드시 헤더에 Authorization 을 넣어주세요!"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정산 완료 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdatePaymentMemberResponse.class))),
        @ApiResponse(responseCode = "409", description = "정산 완료 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCode.class), examples = {
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
            @ExampleObject(name = "PAYMENT_003", description = "정산방에 존재하지 않는 멤버입니다.", value = """
                {
                    "errorCode": "PAYMENT_003",
                    "message": "정산방에 존재하지 않는 멤버입니다."
                }
                """),
            @ExampleObject(name = "PAYMENT_004", description = "이미 정산한 멤버입니다.", value = """
                {
                    "errorCode": "PAYMENT_004",
                    "message": "이미 정산한 멤버입니다."
                }
                """),
            @ExampleObject(name = "PAYMENT_005", description = "멤버가 정산 대상이 아닙니다.", value = """
                {
                    "errorCode": "PAYMENT_004",
                    "message": "멤버가 정산 대상이 아닙니다."
                }
                """),
        })),
    })
    @GetMapping("")
    public ResponseEntity<UpdatePaymentMemberResponse> updatePaymentMembersToComplete(
        @CurrentMember Member member,
        @Parameter(description = "택시팟의 id") @RequestParam("roomId") Long roomId
    ) {
        return ResponseEntity.ok(
            updatePaymentMemberService.updatePaymentMembersToComplete(member, roomId));
    }
}
