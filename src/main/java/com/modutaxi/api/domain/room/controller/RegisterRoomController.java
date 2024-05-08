package com.modutaxi.api.domain.room.controller;


import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto.CreateRoomRequest;
import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.service.RegisterRoomService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/rooms")
@Tag(name = "방 등록", description = "방 등록 API")
public class RegisterRoomController {

    private final RegisterRoomService roomService;

    /**
     * [POST] 방 생성
     */
    @Operation(summary = "모집방 생성", description = "모집방을 생성합니다.<br>목적지 거점의 id, 방 태그, 출발지의 경도, 위도, 출발 시각, 출발지 이름, 목표 인원수를 입력 해주세요.<br>방 태그 : **ONLY_WOMAN**, **MANNER**, **STUDENT_CERTIFICATION**")
    @PostMapping
    public ResponseEntity<RoomDetailResponse> createRoom(
        @CurrentMember Member member,
        @Valid @RequestBody CreateRoomRequest roomRequest) {
        return ResponseEntity.ok(roomService.createRoom(member, roomRequest));
    }
}
