package com.modutaxi.api.domain.spot.repository;

import com.modutaxi.api.domain.spot.entity.Spot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpotRepository extends JpaRepository<Spot, Long> {
}
