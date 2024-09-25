package com.modutaxi.api.domain.banner.controller;


import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponseList;
import com.modutaxi.api.domain.banner.service.GetBannerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banners")
@Tag(name = "배너")
public class GetBannerController {

    private final GetBannerService getBannerService;

    @Operation(summary = "배너 리스트 조회",
        description = "actibe상태의 배너 리스트를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<BannerResponseList> getBannerList() {
        return ResponseEntity.ok(getBannerService.getBannerList());
    }
}
