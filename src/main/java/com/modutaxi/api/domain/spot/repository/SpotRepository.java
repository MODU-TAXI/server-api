package com.modutaxi.api.domain.spot.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.spot.dao.SpotMysqlResponse.SpotWithDistanceResponseInterface;
import com.modutaxi.api.domain.spot.entity.Spot;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpotRepository extends JpaRepository<Spot, Long> {
    Boolean existsByNameEquals(String name);

    @Query("SELECT " +
            "s.id AS id, s.name AS name, s.address AS address, s.spotPoint AS spotpoint, ST_DISTANCE_SPHERE(:point, s.spotPoint) AS distance, (CASE WHEN l.member = :member THEN true ELSE false END) AS liked " +
            "FROM Spot s LEFT JOIN LikedSpot l ON (s.id = l.spot.id) " +
            "WHERE " +
            "s.id = :id")
    Optional<SpotWithDistanceResponseInterface> findByIdWithDistance(@Param("member") Member member, @Param("id") Long id, @Param("point") Point point);

    @Query("SELECT " +
            "s.id AS id, s.name AS name, s.address AS address, s.spotPoint AS spotpoint " +
            "FROM Spot s " +
            "WHERE " +
            "ST_CONTAINS(:polygon, s.spotPoint)")
    List<Spot> findAreaSpotsByPolygon(@Param("polygon") Polygon polygon);

    @Query("SELECT " +
            "s.id AS id, s.spotPoint AS spotPoint " +
            "FROM Spot s " +
            "WHERE " +
            "ST_DISTANCE_SPHERE(:point, s.spotPoint) <= :radius " +
            "ORDER BY " +
            "ST_DISTANCE_SPHERE(:point, s.spotPoint)"
    )
    List<SearchWithRadiusResponseInterface> findNearSpotsInRadius(@Param("point") Point point, @Param("radius") Long radius);

    @Query("SELECT " +
            "s.id AS id, s.name AS name, s.address AS address, s.spotPoint AS spotpoint, ST_DISTANCE_SPHERE(:currentPoint, s.spotPoint) AS distance, (CASE WHEN l.member = :member THEN true ELSE false END) AS liked " +
            "FROM Spot s LEFT JOIN LikedSpot l ON (s.id = l.spot.id) " +
            "ORDER BY " +
            "ST_DISTANCE_SPHERE(:searchPoint, s.spotPoint) "
    )
    List<SpotWithDistanceResponseInterface> findNearSpots(@Param("member") Member member, @Param("currentPoint") Point currentPoint, @Param("searchPoint") Point searchPoint, Pageable pageable);
}