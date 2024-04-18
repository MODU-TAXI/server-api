package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsRoomByRoomManagerId(Long memberId);
    Slice<Room> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
