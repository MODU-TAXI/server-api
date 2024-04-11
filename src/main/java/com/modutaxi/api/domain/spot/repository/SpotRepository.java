package com.modutaxi.api.domain.spot.repository;

import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.entity.Spot;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Boolean existsByNameEquals(String name);

    @Query("SELECT " +
            "s.id AS id, s.name AS name, s.address AS address, s.spotPoint AS spotpoint, ST_DISTANCE_SPHERE(:point, s.spotPoint) AS distance " +
            "FROM Spot s " +
            "WHERE " +
            "s.id = :id")
    Optional<SpotWithDistanceResponseInterface> findByIdWithDistance(@Param("id") Long id, @Param("point") Point point);
}