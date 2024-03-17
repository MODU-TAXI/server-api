package com.modutaxi.api.domain.destination.repository;

import com.modutaxi.api.domain.destination.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DestinationRepository extends JpaRepository<Destination, Long> {

}
