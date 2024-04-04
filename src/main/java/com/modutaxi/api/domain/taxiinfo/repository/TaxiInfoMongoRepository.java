package com.modutaxi.api.domain.taxiinfo.repository;

import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaxiInfoMongoRepository extends MongoRepository<TaxiInfo, Long> {

}
