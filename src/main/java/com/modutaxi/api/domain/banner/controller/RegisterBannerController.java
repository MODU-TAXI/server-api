package com.modutaxi.api.domain.banner.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.banner.dto.BannerRequestDto;
import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponse;
import com.modutaxi.api.domain.banner.service.RegisterBannerService;
import com.modutaxi.api.domain.member.entity.Member;
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
@RequestMapping("/api/banners")
@Tag(name = "배너")
public class RegisterBannerController {
    private final RegisterBannerService registerBannerService;

    @Operation(summary = "배너 생성", description = "배너를 생성합니다. imageUrl, linkUrl을 넣어주세요. 초기 Active 값은 false입니다.")
    @PostMapping
    public ResponseEntity<BannerResponse> registerBanner(
        @Valid @RequestBody BannerRequestDto.CreateBannerRequest createBannerRequest) {
        return ResponseEntity.ok(registerBannerService.createBanner(createBannerRequest));
    }
}