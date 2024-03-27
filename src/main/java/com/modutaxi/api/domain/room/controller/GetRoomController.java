package com.modutaxi.api.domain.room.controller;

import com.google.gson.JsonObject;
import com.modutaxi.api.domain.room.service.GetRoomService;
import com.modutaxi.api.domain.room.service.GetTaxiInfoService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class GetRoomController {

    private final GetRoomService getRoomService;
    private final GetTaxiInfoService getTaxiInfoService;

    @GetMapping("/driving")
    public ResponseEntity<?> getDrivingInfo() {
        return ResponseEntity.ok(getTaxiInfoService.getDrivingInfo());
    }
}
