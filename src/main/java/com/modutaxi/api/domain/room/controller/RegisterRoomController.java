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
    @Operation(summary = "모집방 업데이트",
        description = "**RoomTag Enum 종류**\n\n**RoomTagBitMask**: "
            + "ONLY\\_WOMAN, ONLY\\_MAN, REGARDLESS\\_OF\\_GENDER, STUDENT\\_CERTIFICATION"
            + "\n\n\nPoint(X:경도, Y:위도)\n\n**예시: Point(127.xxx,36.xxx)**")
    @PostMapping
    public ResponseEntity<RoomDetailResponse> createRoom(
        @CurrentMember Member member,
        @Valid @RequestBody CreateRoomRequest roomRequest) {
        return ResponseEntity.ok(roomService.createRoom(member, roomRequest));
    }
}
