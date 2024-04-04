package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.service.UpdateRoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
@Tag(name = "방 정보 업데이트", description = "방 정보 업데이트 및 삭제")
public class UpdateRoomController {

    private final UpdateRoomService updateRoomService;

    /**
     * [Patch] 방 정보 수정
     */
    @Operation(summary = "모집방 업데이트")
    @PatchMapping("/{id}")
    public ResponseEntity<RoomDetailResponse> updateRoom(
        @CurrentMember Member member,
        @PathVariable Long id,
        @Valid @RequestBody RoomRequestDto.UpdateRoomRequest updateRoomRequest) {
        return ResponseEntity.ok(updateRoomService.updateRoom(member, id, updateRoomRequest));
    }

    /**
     * [Delete] 방 삭제
     */
    @Operation(summary = "모집방 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoom(
        @CurrentMember Member member,
        @PathVariable Long id
    ) {
        updateRoomService.deleteRoom(member, id);
        return ResponseEntity.ok("삭제 되었습니다.");
    }
}
