package com.modutaxi.api.domain.roomwaiting;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.roomwaiting.RoomWaitingResponseDto.MemberRoomInResponse;
import com.modutaxi.api.domain.roomwaiting.RoomWaitingResponseDto.RoomWaitingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import retrofit2.http.Path;

@RestController
@RequiredArgsConstructor
@Tag(name = "방 참가, 대기열 어쩌구 저쩌구 API")
public class RoomWaitingController {

    private final RoomWaitingService roomWaitingService;

    @Operation(summary = "방 입장 요청")
    @PostMapping("/room-waiting/{roomId}/members")
    public ResponseEntity<String> applyForParticipate(@CurrentMember Member member,
            @PathVariable String roomId){
        return ResponseEntity.ok(roomWaitingService.applyForParticipate(member, roomId));
    }

    @Operation(summary = "대기열 리스트 조회")
    @GetMapping("/room-waiting/{roomId}")
    public ResponseEntity<List<RoomWaitingResponse>> getWaitingList(@CurrentMember Member member,
            @PathVariable Long roomId){
        return ResponseEntity.ok(roomWaitingService.getWaitingList(roomId));
    }

    @Operation(summary = "특정 방 참가 리스트 조회")
    @GetMapping("/room-in/{roomId}/members")
    public ResponseEntity<List<MemberRoomInResponse>> acceptForParticipate(
            @CurrentMember Member member,
            @PathVariable String roomId){
        return ResponseEntity.ok(roomWaitingService.getParticipateInRoom(Long.valueOf(roomId)));
    }

    @Operation(summary = "특정 사용자 입장 수락")
    @DeleteMapping("/room-waiting/{roomId}/members/{memberId}")
    public ResponseEntity<String> acceptForParticipate(
            @CurrentMember Member member,
            @PathVariable String roomId,
            @PathVariable String memberId){
        return ResponseEntity.ok(roomWaitingService.acceptForParticipate(member, roomId, memberId));
    }


}
