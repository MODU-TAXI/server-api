package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsRoomByRoomManagerId(Long memberId);
    @Query("SELECT r FROM Room r WHERE BITAND(r.roomTagBitMask, :tagBitMask) = :tagBitMask AND r.roomStatus = 0 AND ( :isImminent = false OR r.departureTime BETWEEN :timeAfter AND :timeBefore ) AND ( :spotId IS NULL OR :spotId = r.spot.id ) ORDER BY r.createdAt DESC")
    Slice<Room> findAllWhereTagBitMaskOrderByCreatedAtDesc(Long spotId, Integer tagBitMask, Boolean isImminent, LocalDateTime timeAfter, LocalDateTime timeBefore, Pageable pageable);

}
