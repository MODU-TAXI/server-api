package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.dao.RoomMysqlResponse;
import com.modutaxi.api.domain.room.dao.RoomMysqlResponse.SearchWithRadiusResponseInterface;
import com.modutaxi.api.domain.room.entity.Room;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsRoomByRoomManagerId(Long memberId);

    Slice<Room> findAllByOrderByCreatedAtDesc(Pageable pageable);


    @Query("SELECT " +
            "r.id AS id, r.departurePoint AS departurePoint, s.name AS spotName " +
            "FROM Room r LEFT JOIN Spot s ON (r.spot = s)" +
            "WHERE " +
            "ST_DISTANCE_SPHERE(:point, r.departurePoint) <= :radius " +
            "ORDER BY " +
            "ST_DISTANCE_SPHERE(:point, r.departurePoint)"
    )
    List<SearchWithRadiusResponseInterface> findNearRoomsInRadius(@Param("point") Point point, @Param("radius") Long radius);
}
