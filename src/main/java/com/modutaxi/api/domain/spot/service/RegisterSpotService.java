package com.modutaxi.api.domain.spot.service;

import com.modutaxi.api.domain.spot.repository.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterSpotService {
    private final SpotRepository spotRepository;
}
