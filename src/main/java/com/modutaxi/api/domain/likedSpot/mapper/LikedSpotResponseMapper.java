package com.modutaxi.api.domain.likedSpot.mapper;

import com.modutaxi.api.domain.likedSpot.dao.LikedSpotMysqlResponse.LikedSpotResponseInterface;
import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotListResponse;
import org.springframework.data.geo.Point;

public class LikedSpotResponseMapper {
    public static LikedSpotListResponse toDto(LikedSpotResponseInterface dao) {
        return new LikedSpotListResponse(dao.getSpotId(), dao.getSpotName(), (float)dao.getSpotpoint().getX(), (float)dao.getSpotpoint().getY());
    }
}
