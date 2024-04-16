package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateSpotService {
    private final SpotRepository spotRepository;
    private final GetSpotService getSpotService;

    @Transactional
    public Long deleteSpot(Long id) {
        spotRepository.deleteById(id);
        return id;
    }

    @Transactional
    public Long updateSpot(Long id, String name, String address, Point point) {
        if (spotRepository.existsByNameEquals(name)) {
            throw new BaseException(SpotError.SPOT_NAME_DUPLICATED);
        }
        Spot spot = getSpotService.getSpot(id);
        spot.updateSpotInfo(name, address, point);
        return id;
    }
}