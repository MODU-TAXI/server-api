package com.modutaxi.api.domain.taxiinfo.service;

import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterTaxiInfoService {

    private final TaxiInfoMongoRepository taxiInfoMongoRepository;

    public void savePath(Long id, List<Point> path) {
        taxiInfoMongoRepository.save(TaxiInfo.toEntity(id, path));
    }
}
