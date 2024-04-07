package com.modutaxi.api.domain.taxiinfo.service;

import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import com.mongodb.client.model.geojson.LineString;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterTaxiInfoService {

    private final TaxiInfoMongoRepository taxiInfoMongoRepository;

    public void savePath(Long id, LineString path) {
        taxiInfoMongoRepository.save(TaxiInfo.toEntity(id, path));
    }
}
