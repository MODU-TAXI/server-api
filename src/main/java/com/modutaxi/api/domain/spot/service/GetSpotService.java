package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.*;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.mapper.SpotResponseMapper;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public GetSpotWithDistanceResponse getSpot(Member member, Long id, Point point) {
        SpotWithDistanceResponseInterface spot = spotRepository.findByIdWithDistance(member, id, point).orElseThrow(
            () -> new BaseException(SPOT_ID_NOT_FOUND)
        );
        return SpotResponseMapper.toSpotWithDistanceResponse(spot);
    }

    public GetSpotResponses getAreaSpots(Polygon polygon) {
        List<Spot> spots = spotRepository.findAreaSpotsByPolygon(polygon);
        List<GetSpotResponse> spotList = spots.stream().map(SpotResponseMapper::toGetSpotResponse).toList();
        return new GetSpotResponses(spotList);
    }

    public SearchSpotWithRadiusResponses getRadiusSpots(Point searchPoint, int mapSearchLimit) {
        List<SearchWithRadiusResponseInterface> spots = spotRepository.findNearSpotsInRadius(searchPoint, mapSearchLimit);
        List<SearchSpotWithRadiusResponse> spotList = spots.stream().map(SpotResponseMapper::toSearchWithRadiusResponse).toList();
        Double[] startEndPoints = getStartEndPoints(searchPoint, spotList);
        return new SearchSpotWithRadiusResponses(startEndPoints[0], startEndPoints[1], startEndPoints[2], startEndPoints[3], spotList);
    }

    public GetSpotWithDistanceResponses getNearSpots(Member member, Point currentPoint, Point searchPoint, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<SpotWithDistanceResponseInterface> spots = spotRepository.findNearSpots(member, currentPoint, searchPoint, pageable);
        List<GetSpotWithDistanceResponse> spotList = spots.stream().map(SpotResponseMapper::toSpotWithDistanceResponse).toList();
        return new GetSpotWithDistanceResponses(spotList);
    }

    private Double[] getStartEndPoints(Point searchPoint, List<SearchSpotWithRadiusResponse> spotList) {
        double minLongitude = searchPoint.getX(), maxLongitude = searchPoint.getX(), minLatitude = searchPoint.getY(), maxLatitude = searchPoint.getY();
        for(SearchSpotWithRadiusResponse spot : spotList) {
            minLongitude = minLongitude < spot.getLongitude() ? minLongitude : spot.getLongitude();
            maxLongitude = maxLongitude > spot.getLongitude() ? maxLongitude : spot.getLongitude();
            minLatitude = minLatitude < spot.getLatitude() ? minLatitude : spot.getLatitude();
            maxLatitude = maxLatitude > spot.getLatitude() ? maxLatitude : spot.getLatitude();
        }
        return new Double[]{minLongitude, minLatitude, maxLongitude, maxLatitude};
    }
}