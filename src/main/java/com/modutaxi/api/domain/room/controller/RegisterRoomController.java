package com.modutaxi.api.domain.room.controller;


import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.dto.RoomRequestDto;
import com.modutaxi.api.domain.room.service.RegisterRoomService;
import io.swagger.v3.oas.annotations.Operation;

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
public class RegisterRoomController {
    private final RegisterRoomService roomService;
    @Operation(summary = "모집방 생성")
    @PostMapping
    public ResponseEntity<?> createRoom (
        @CurrentMember Member member,
        @Valid @RequestBody RoomRequestDto.CreateRoomRequest roomRequest) {
        roomService.createRoom(member, roomRequest);
        return ResponseEntity.ok("성공했어욥");
    }
}
