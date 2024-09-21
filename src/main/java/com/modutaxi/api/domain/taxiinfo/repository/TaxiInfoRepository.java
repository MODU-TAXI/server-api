package com.modutaxi.api.domain.taxiinfo.repository;

import com.modutaxi.api.domain.taxiinfo.entity.TaxiInfo;
import org.springframework.data.repository.CrudRepository;

public interface TaxiInfoRepository extends CrudRepository<TaxiInfo, Long> {

}
