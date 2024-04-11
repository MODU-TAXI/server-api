package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateSpotService {
    private final SpotRepository spotRepository;

    @Transactional
    public Long deleteSpot(Long id) {
        spotRepository.deleteById(id);
        return id;
    }
}