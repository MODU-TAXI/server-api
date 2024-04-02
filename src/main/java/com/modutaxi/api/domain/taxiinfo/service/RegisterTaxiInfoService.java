package com.modutaxi.api.domain.taxiinfo.service;

import com.modutaxi.api.domain.taxiinfo.entity.Point;
import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import com.modutaxi.api.domain.taxiinfo.repository.TaxiInfoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RegisterTaxiInfoService {
    private final TaxiInfoRepository taxiInfoRepository;
    public void savePath(Long id, List<Point> path){
        taxiInfoRepository.save(TaxiInfo.toEntity(id, path));
    }
}
