package com.modutaxi.api.domain.paymentroom.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomRequestDto.PaymentRoomRequest;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.RegisterPaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.service.RegisterPaymentRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "정산", description = "정산 요청 API")
@RequestMapping("/api/payment-room")
public class RegisterPaymentRoomController {

    private final RegisterPaymentRoomService registerPaymentRoomService;

    /**
     * [POST] 정산 요청
     */
    @Operation(summary = "정산 요청", description = "방장이 정산을 요청할 때 사용하는 API입니다. "
        + "<br>participantList에는 같이 탑승한 멤버들의 Id를, nonParticipantList에는 같이 탑승하지 않은 멤버들의 Id를 보내주세요."
        + "<br>이전 화면에서 계좌 등록 API를 실행하시고, 반환된 response에서 id를 받아와 accountId에 넣어주세요!"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "400", description = "정산 요청 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCode.class), examples = {
            @ExampleObject(name = "PRT_003", description = "탑승 list의 멤버 Id가 유효하지 않음", value = """
                {
                    "errorCode": "PRT_01",
                    "message": "해당 사용자가 대기열에 없습니다."
                }
                """),
        })),
        @ApiResponse(responseCode = "409", description = "정산 요청 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_001", description = "탑승 list의 멤버 Id가 존재하지 않음", value = """
                {
                    "errorCode": "MEMBER_001",
                    "message": "존재하지 않는 사용자입니다."
                }
                """),
            @ExampleObject(name = "ROOM_001", description = "존재하지 않는 방", value = """
                {
                    "errorCode": "ROOM_01",
                    "message": "존재하지 않는 방입니다."
                }
                """),
            @ExampleObject(name = "ROOM_002", description = "방장이 아닌 사람이 정산을 요청", value = """
                {
                    "errorCode": "ROOM_02",
                    "message": "권한이 없는 사용자입니다."
                }
                """),
            @ExampleObject(name = "PAYMENT_001", description = "존재하지 않는 계좌", value = """
                {
                    "errorCode": "PAYMENT_001",
                    "message": "존재하지 않는 계좌입니다."
                }
                """),
        })),
    })
    @PostMapping
    public ResponseEntity<RegisterPaymentRoomResponse> registerPaymentRoom(
        @CurrentMember Member member,
        @Valid @RequestBody PaymentRoomRequest request) {
        return ResponseEntity.ok(registerPaymentRoomService.register(member, request));
    }

}
