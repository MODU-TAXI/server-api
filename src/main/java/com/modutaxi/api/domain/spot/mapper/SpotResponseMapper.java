package com.modutaxi.api.domain.spot.mapper;

import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotResponse;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotWithDistanceResponse;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.SearchWithRadiusResponse;
import com.modutaxi.api.domain.spot.entity.Spot;
import org.springframework.data.geo.Point;

public class SpotResponseMapper {
    public static GetSpotWithDistanceResponse toSpotWithDistanceResponse(SpotWithDistanceResponseInterface spot) {
        return new GetSpotWithDistanceResponse(spot.getId(), spot.getName(), spot.getAddress(), new Point(spot.getSpotpoint().getX(), spot.getSpotpoint().getY()), spot.getDistance());
    }

    public static GetSpotResponse toGetSpotResponse(Spot spot) {
        return new GetSpotResponse(spot.getId(), spot.getName(), spot.getAddress(), new Point(spot.getSpotPoint().getX(), spot.getSpotPoint().getY()));
    }

    public static SearchWithRadiusResponse toSearchWithRadiusResponse(SearchWithRadiusResponseInterface spot) {
        return new SearchWithRadiusResponse(spot.getId(), new Point(spot.getSpotpoint().getX(), spot.getSpotpoint().getY()));
    }
}