package com.modutaxi.api.domain.roomwaiting.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.ApplyResponse;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.MemberRoomInResponseList;
import com.modutaxi.api.domain.roomwaiting.mapper.RoomWaitingMapper.RoomWaitingResponseList;
import com.modutaxi.api.domain.roomwaiting.service.RoomWaitingService;
import io.swagger.v3.oas.annotations.Operation;
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
    @PostMapping("/api/rooms/{roomId}/apply")
    public ResponseEntity<ApplyResponse> applyForParticipate(@CurrentMember Member member,
                                                             @PathVariable String roomId) {
        return ResponseEntity.ok(roomWaitingService.applyForParticipate(member, roomId));
    }

    @Operation(summary = "대기열 리스트 조회")
    @GetMapping("/api/rooms/{roomId}/members/waiting")
    public ResponseEntity<RoomWaitingResponseList> getWaitingList(@PathVariable Long roomId) {
        return ResponseEntity.ok(roomWaitingService.getWaitingList(roomId));
    }

    @Operation(summary = "특정 방 참가 리스트 조회")
    @GetMapping("/api/rooms/{roomId}/members/in")
    public ResponseEntity<MemberRoomInResponseList> acceptForParticipate(@PathVariable String roomId) {
        return ResponseEntity.ok(roomWaitingService.getParticipateInRoom(Long.valueOf(roomId)));
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
