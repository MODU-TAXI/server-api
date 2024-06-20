package com.modutaxi.api.domain.roomwaiting.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.ApplyResponse;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.RoomWaitingResponseList;
import com.modutaxi.api.domain.roomwaiting.service.RoomWaitingService;
import io.swagger.v3.oas.annotations.Operation;
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
@Tag(name = "방 참가")
public class RoomWaitingController {

    private final RoomWaitingService roomWaitingService;

    @Operation(summary = "방 입장 요청")
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "방 입장 불가", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_010", description = "신고 누적으로 인해 임시 차단", value = """
                {
                    "errorCode": "MEMBER_010",
                    "message": "임시 차단된 사용자입니다."
                }
                """),
        })),
    })
    @PostMapping("/api/rooms/{roomId}/apply")
    public ResponseEntity<ApplyResponse> applyForParticipate(@CurrentMember Member member,
        @PathVariable String roomId) {
        return ResponseEntity.ok(roomWaitingService.applyForParticipate(member.getId(), roomId));
    }

    @Operation(summary = "대기열 리스트 조회")
    @GetMapping("/api/rooms/{roomId}/members/waiting")
    public ResponseEntity<RoomWaitingResponseList> getWaitingList(@CurrentMember Member member,
        @PathVariable Long roomId) {
        return ResponseEntity.ok(roomWaitingService.getWaitingList(member.getId(), roomId));
    }

    @Operation(summary = "대기열에서 퇴장")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "대기열에서 퇴장 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponse.class))),
        @ApiResponse(responseCode = "400", description = "대기열에서 퇴장 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_001", value = """
                {
                    "code": "MEMBER_001",
                    "message": "존재하지 않는 사용자입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "존재하지 않는 사용자입니다.")
        })),
        @ApiResponse(responseCode = "400", description = "대기열에서 퇴장 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomErrorCode.class), examples = {
            @ExampleObject(name = "ROOM_001", value = """
                {
                    "code": "ROOM_001",
                    "message": "존재하지 않는 방입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "존재하지 않는 방(택시팟)입니다.")
        })),
        @ApiResponse(responseCode = "400", description = "해당 사용자가 대기열에 없습니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ParticipateErrorCode.class), examples = {
            @ExampleObject(name = "PRT_003", value = """
                {
                    "code": "PRT_003",
                    "message": "해당 사용자가 대기열에 없습니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "해당 방의 대기 리스트에 해당되는 멤버가 존재하지 않습니다.")
        })),
    })
    @DeleteMapping("/api/rooms/{roomId}/waiting")
    public ResponseEntity<DeleteResponse> leaveRoomWaiting(
        @CurrentMember Member member,
        @PathVariable Long roomId) {
        return ResponseEntity.ok(roomWaitingService.leaveRoomWaiting(member.getId(), roomId));
    }
}
