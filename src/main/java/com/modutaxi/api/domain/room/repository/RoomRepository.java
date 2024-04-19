package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsRoomByRoomManagerId(Long memberId);
    @Query("SELECT r FROM Room r WHERE BITAND(r.roomTagBitMask, :tagBitMask) = :tagBitMask AND r.roomStatus = 0 ORDER BY r.createdAt DESC")
    Slice<Room> findAllWhereTagBitMaskOrderByCreatedAtDesc(Integer tagBitMask, Pageable pageable);
    @Query("SELECT r FROM Room r WHERE BITAND(r.roomTagBitMask, :tagBitMask) = :tagBitMask AND r.roomStatus = 0 AND r.spot.id = :spotId ORDER BY r.createdAt DESC")
    Slice<Room> findAllWhereTagBitMaskAndSpotIdOrderByCreatedAtDesc(Long spotId, Integer tagBitMask, Pageable pageable);
    @Query("SELECT r FROM Room r WHERE BITAND(r.roomTagBitMask, :tagBitMask) = :tagBitMask AND r.roomStatus = 0 AND r.departureTime BETWEEN :timeAfter AND :timeBefore ORDER BY r.createdAt DESC")
    Slice<Room> findAllWhereBetweenTimeAndTagBitMaskOrderByCreatedAtDesc(Integer tagBitMask, LocalDateTime timeAfter, LocalDateTime timeBefore, Pageable pageable);
    @Query("SELECT r FROM Room r WHERE BITAND(r.roomTagBitMask, :tagBitMask) = :tagBitMask AND r.roomStatus = 0 AND r.departureTime BETWEEN :timeAfter AND :timeBefore AND r.spot.id = :spotId ORDER BY r.createdAt DESC")
    Slice<Room> findAllWhereBetweenTimeAndTagBitMaskAndSpotIdOrderByCreatedAtDesc(Long spotId, Integer tagBitMask, LocalDateTime timeAfter, LocalDateTime timeBefore, Pageable pageable);

}
