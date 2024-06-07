package com.modutaxi.api.domain.roomwaiting.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.ApplyResponse;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.MemberRoomInResponseList;
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
        return ResponseEntity.ok(roomWaitingService.applyForParticipate(member, roomId));
    }

    @Operation(summary = "대기열 리스트 조회")
    @GetMapping("/api/rooms/{roomId}/members/waiting")
    public ResponseEntity<RoomWaitingResponseList> getWaitingList(@CurrentMember Member member,
                                                                  @PathVariable Long roomId) {
        return ResponseEntity.ok(roomWaitingService.getWaitingList(member, roomId));
    }

    @Operation(summary = "특정 방 참가 리스트 조회")
    @GetMapping("/api/rooms/{roomId}/members/in")
    public ResponseEntity<MemberRoomInResponseList> getParticipateInRoom(@CurrentMember Member member,
                                                                         @PathVariable String roomId) {
        return ResponseEntity.ok(roomWaitingService.getParticipateInRoom(member, Long.valueOf(roomId)));
    }

    @Operation(summary = "특정 사용자 입장 수락")
    @DeleteMapping("/api/rooms/{roomId}/members/{memberId}/approve")
    public ResponseEntity<ApplyResponse> acceptForParticipate(
        @CurrentMember Member member,
        @PathVariable String roomId,
        @PathVariable String memberId) {
        return ResponseEntity.ok(roomWaitingService.acceptForParticipate(member, roomId, memberId));
    }
}
