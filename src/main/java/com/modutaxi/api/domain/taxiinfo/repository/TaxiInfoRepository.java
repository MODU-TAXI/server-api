package com.modutaxi.api.domain.taxiinfo.repository;

import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaxiInfoRepository extends MongoRepository<TaxiInfo, Long> {
}
