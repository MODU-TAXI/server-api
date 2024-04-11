package com.modutaxi.api.domain.spot.mapper;

import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotWithDistanceResponse;
import org.springframework.data.geo.Point;

public class SpotResponseMapper {
    public static GetSpotWithDistanceResponse toSpotWithDistanceResponse(SpotWithDistanceResponseInterface spot) {
        return new GetSpotWithDistanceResponse(spot.getId(), spot.getName(), spot.getAddress(), new Point(spot.getSpotpoint().getX(), spot.getSpotpoint().getY()), spot.getDistance());
    }
}