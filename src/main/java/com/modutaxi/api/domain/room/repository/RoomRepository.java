package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByRoomManagerId(Long memberId);
    boolean existsRoomByRoomManagerId(Long memberId);
}
