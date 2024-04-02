package com.modutaxi.api.domain.room.controller;

import com.modutaxi.api.domain.room.dto.RoomResponseDto.RoomDetailResponse;
import com.modutaxi.api.domain.room.service.GetRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class GetRoomController {

    private final GetRoomService getRoomService;
    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDetailResponse> getTaxiInfo(@PathVariable Long roomId){
        return ResponseEntity.ok(getRoomService.getRoomDetail(roomId));
    }
}