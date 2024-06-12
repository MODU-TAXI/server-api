package com.modutaxi.api.domain.paymentroom.controller;

import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.PaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.service.GetPaymentRoomService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "정산", description = "정산 조회 API")
@RequestMapping("/api/payment-rooms")
public class GetPaymentRoomController {

    private final GetPaymentRoomService getPaymentRoomService;

    /**
     * [GET] 정산 정보 조회
     */
    @Operation(
        summary = "정산 정보 조회",
        description = "정산 정보(계좌 정보, 금액 정보)를 조회합니다.<br>" +
            "조회할 택시팟의 Id(roomId)를 보내주세요.<br>" +
            "정산방 status 종류: COMPLETE(정산 완료), INCOMPLETE(정산 미완료)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "정산 정보 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentRoomResponse.class))),
        @ApiResponse(responseCode = "409", description = "정산 정보 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCode.class), examples = {
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
    @GetMapping("/{roomId}")
    public ResponseEntity<PaymentRoomResponse> getPaymentRoom(
        @Parameter(description = "조회할 택시팟의 id")
        @PathVariable("roomId") Long roomId
    ) {
        return ResponseEntity.ok(getPaymentRoomService.getPaymentRoom(roomId));
    }
}
