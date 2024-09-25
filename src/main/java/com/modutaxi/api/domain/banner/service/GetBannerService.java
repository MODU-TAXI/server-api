package com.modutaxi.api.domain.banner.service;

import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponse;
import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponseList;
import com.modutaxi.api.domain.banner.entity.Banner;
import com.modutaxi.api.domain.banner.mapper.BannerMapper;
import com.modutaxi.api.domain.banner.repository.BannerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetBannerService {

    private final BannerRepository bannerRepository;

    public BannerResponseList getBannerList() {

        List<Banner> banners = bannerRepository.findAllByActiveTrue();

        List<BannerResponse> bannerResponses =
            banners.stream().map(BannerMapper::toDto).toList();

        return BannerMapper.toDtoList(bannerResponses);
    }
}
