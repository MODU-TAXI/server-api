package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.room.entity.Room;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByRoomManagerId(Long memberId);

    boolean existsRoomByRoomManagerId(Long memberId);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT r FROM Room r WHERE r.id =:id AND r.roomStatus < 4")
    Optional<Room> findByIdAndRoomStatusIsNotDelete(Long id);
}
