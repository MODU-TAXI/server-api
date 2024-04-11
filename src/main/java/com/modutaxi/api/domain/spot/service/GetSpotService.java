package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
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
}