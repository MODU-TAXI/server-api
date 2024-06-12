package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.UpdateRoomStatusRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.DeleteRoomResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.UpdateRoomResponse;
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
    @Operation(summary = "모집방 업데이트", description = "모집방의 데이터를 업데이트합니다.<br>수정할 데이터가 있다면, 목적지 거점의 id, 방 태그, 출발지의 경도, 위도, 출발 시각, 출발지 이름, 목표 인원수를 입력 해주세요.<br>방 태그 : **ONLY_WOMAN**, **MANNER**, **STUDENT_CERTIFICATION**")
    @PatchMapping("/{id}")
    public ResponseEntity<RoomDetailResponse> updateRoom(
        @CurrentMember Member member,
        @PathVariable Long id,
        @Valid @RequestBody UpdateRoomRequest updateRoomRequest) {
        return ResponseEntity.ok(updateRoomService.updateRoom(member, id, updateRoomRequest));
    }

    /**
     * [Delete] 방 삭제
     */
    @Operation(summary = "모집방 삭제", description = "삭제할 모집방의 id를 입력해주세요.")
    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteRoomResponse> deleteRoom(
        @CurrentMember Member member,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(updateRoomService.deleteRoom(member, id));
    }

    /**
     * [PATCH] 매칭 완료
     */
    @Operation(summary = "매칭 완료", description = "모집방의 현재 상태를 매칭완료 상태로 변경합니다.")
    @PatchMapping("/finish/matching/{id}")
    public ResponseEntity<UpdateRoomResponse> updateRoomStatus(
        @CurrentMember Member member,
        @PathVariable Long id
    ) {
        return ResponseEntity.ok(
            updateRoomService.finishMatching(member, id));
    }
}
