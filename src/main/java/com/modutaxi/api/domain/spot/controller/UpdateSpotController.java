package com.modutaxi.api.domain.spot.controller;


import com.modutaxi.api.domain.spot.service.UpdateSpotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
@Tag(name = "거점 수정", description = "거점 수정 API")
public class UpdateSpotController {
    private final UpdateSpotService updateSpotService;
}