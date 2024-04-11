package com.modutaxi.api.domain.spot.controller;


import com.modutaxi.api.domain.spot.service.RegisterSpotService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots")
@Tag(name = "거점 등록", description = "거점 등록 API")
public class RegisterSpotController {
    private final RegisterSpotService registerSpotService;
}