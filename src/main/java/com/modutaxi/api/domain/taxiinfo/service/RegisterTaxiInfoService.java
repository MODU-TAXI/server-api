package com.modutaxi.api.domain.taxiinfo.service;

import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoRepository;
import com.mongodb.client.model.geojson.LineString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterTaxiInfoService {

    private final TaxiInfoRepository taxiInfoRepository;

    public void savePath(Long id, LineString path) {
        taxiInfoRepository.save(TaxiInfo.toEntity(id, path));
    }
}
