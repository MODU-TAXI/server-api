package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.mapper.SpotMapper;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterSpotService {
    private final SpotRepository spotRepository;

    @Transactional
    public Long registerDestination(String name, String address, Point spotPoint) {
        if (spotRepository.existsByNameEquals(name)) {
            throw new BaseException(SpotError.SPOT_NAME_DUPLICATED);
        }
        Spot newSpot = spotRepository.save(SpotMapper.toEntity(name, address, spotPoint));
        return newSpot.getId();
    }
}