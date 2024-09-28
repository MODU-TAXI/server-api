package com.modutaxi.api.domain.banner.mapper;

import com.modutaxi.api.domain.banner.dto.BannerRequestDto.CreateBannerRequest;
import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponse;
import com.modutaxi.api.domain.banner.dto.BannerResponseDto.BannerResponseList;
import com.modutaxi.api.domain.banner.entity.Banner;
import java.util.List;


public class BannerMapper {

    public static Banner toEntity(CreateBannerRequest createBannerRequest) {
        return Banner.builder()
            .imageUrl(createBannerRequest.getImageUrl())
            .linkUrl(createBannerRequest.getLinkUrl())
            .build();
    }


    public static BannerResponse toDto(Banner banner){
        return BannerResponse.builder()
            .id(banner.getId())
            .imageUrl(banner.getImageUrl())
            .linkUrl(banner.getLinkUrl())
            .active(banner.isActive())
            .build();
    }


    public static BannerResponseList toDtoList(List<BannerResponse> bannerResponses){
        return BannerResponseList.builder()
            .BannerResponseList(bannerResponses)
            .build();
    }
}

