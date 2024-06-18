package com.modutaxi.api.domain.roomwaiting.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.ChatErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ParticipateErrorCode;
import com.modutaxi.api.common.exception.errorcode.RoomErrorCode;
import com.modutaxi.api.domain.chat.dto.ChatResponseDto.DeleteResponse;
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


    @Operation(summary = "현재 내가 참여하고 있는 방 퇴장", description = "현재 참여해있는 채팅방에서 퇴장하고, 퇴장 메시지를 채팅방에 전송합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "현재 내가 참여하고 있는 방 퇴장성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteResponse.class))),
        @ApiResponse(responseCode = "400", description = "현재 내가 참여하고 있는 방 퇴장 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatErrorCode.class), examples = {
            @ExampleObject(name = "CHAT_004", value = """
                {
                    "code": "CHAT_004",
                    "message": "방에서 이미 나간 상태입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "이미 방에서 나간 상태이거나 다시 퇴장하려할 때 혹은 참여해있는 방이 존재하지 않을 때 생기는 에러입니다.")
        })),
        @ApiResponse(responseCode = "400", description = "현재 내가 참여하고 있는 방 퇴장 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomErrorCode.class), examples = {
            @ExampleObject(name = "ROOM_001", value = """
                {
                    "code": "ROOM_001",
                    "message": "존재하지 않는 방입니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "존재하지 않는 방(택시팟)입니다.")
        })),
        @ApiResponse(responseCode = "400", description = "방장은 퇴장할 수 없습니다. 방장은 오로지 삭제만 가능합니다.", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoomErrorCode.class), examples = {
            @ExampleObject(name = "ROOM_008", value = """
                {
                    "code": "ROOM_008",
                    "message": 방장은 퇴장할 수 없습니다. 방장은 오로지 삭제만 가능합니다.",
                    "timeStamp": "2024-04-04T02:48:31.646102"
                }
                """, description = "방장은 퇴장할 수 없습니다. 방장은 오로지 삭제만 가능합니다.")
        })),
    })
    @DeleteMapping("/api/rooms")
    public ResponseEntity<DeleteResponse> deleteChatRoomInfo(@CurrentMember Member member) {
        return ResponseEntity.ok(roomWaitingService.leaveRoomAndDeleteChatRoomInfo(member.getId()));
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
        @PathVariable String roomId) {
        return ResponseEntity.ok(roomWaitingService.leaveRoomWaiting(member.getId(), roomId));
    }
}
