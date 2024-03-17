package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
