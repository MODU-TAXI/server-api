package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.*;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.mapper.SpotResponseMapper;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.modutaxi.api.common.exception.errorcode.SpotError.SPOT_ID_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class GetSpotService {
    private final SpotRepository spotRepository;

    public Spot getSpot(Long id) {
        return spotRepository.findById(id).orElseThrow(
                () -> new BaseException(SPOT_ID_NOT_FOUND)
        );
    }

    public GetSpotWithDistanceResponse getSpot(Long id, Point point) {
        SpotWithDistanceResponseInterface spot = spotRepository.findByIdWithDistance(id, point).orElseThrow(
                () -> new BaseException(SPOT_ID_NOT_FOUND)
        );
        return SpotResponseMapper.toSpotWithDistanceResponse(spot);
    }

    public GetSpotResponses getAreaSpots(Polygon polygon) {
        List<Spot> spots = spotRepository.findAreaSpotsByPolygon(polygon);
        List<GetSpotResponse> spotList = spots.stream().map(SpotResponseMapper::toGetSpotResponse).toList();
        return new GetSpotResponses(spotList);
    }

    public SearchWithRadiusResponses getRadiusSpots(Point searchPoint, Long radius) {
        List<SearchWithRadiusResponseInterface> spots = spotRepository.findNearSpotsInRadius(searchPoint, radius);
        List<SearchWithRadiusResponse> spotList = spots.stream().map(SpotResponseMapper::toSearchWithRadiusResponse).toList();
        return new SearchWithRadiusResponses(spotList);
    }

    public GetSpotWithDistanceResponses getNearSpots(Point point, Long count) {
        List<SpotWithDistanceResponseInterface> spots = spotRepository.findNearSpots(point, count);
        List<GetSpotWithDistanceResponse> spotList = spots.stream().map(SpotResponseMapper::toSpotWithDistanceResponse).toList();
        return new GetSpotWithDistanceResponses(spotList);
    }
}