package com.modutaxi.api.domain.banner.service;

import com.modutaxi.api.domain.banner.dto.BannerRequestDto.CreateBannerRequest;
import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponse;
import com.modutaxi.api.domain.banner.mapper.BannerMapper;
import com.modutaxi.api.domain.banner.repository.BannerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterBannerService {

    private final BannerRepository bannerRepository;
    @Transactional
    public BannerResponse createBanner(CreateBannerRequest createBannerRequest) {
        return BannerMapper.toDto(bannerRepository.save(BannerMapper.toEntity(createBannerRequest)));
    }
}