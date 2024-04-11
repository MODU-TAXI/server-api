package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.dto.SpotResponseDto.GetSpotWithDistanceResponse;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.mapper.SpotResponseMapper;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

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
}