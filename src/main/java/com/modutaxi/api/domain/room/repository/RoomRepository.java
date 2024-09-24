package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findAllByRoomManagerId(Long memberId);

    @Query(value = "SELECT CASE WHEN COUNT(1) > 0 THEN TRUE ELSE FALSE END FROM Room r WHERE r.roomManager =:member AND r.roomStatus < 4")
    boolean existsRoomByRoomManager(Member member);

    @Query(value = "SELECT r FROM Room r WHERE r.id =:id AND r.roomStatus < 4")
    Optional<Room> findByIdAndRoomStatusIsNotDelete(Long id);

    @Query(value = "SELECT r.id FROM Room r WHERE r.roomManager =:member AND r.roomStatus < 4")
    Long findIdByRoomManagerAndRoomStatusIsNotDelete(Member member);

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "SELECT r FROM Room r WHERE r.id =:id AND r.roomStatus < 4")
    Optional<Room> findActiveRoomByIdForUpdate(Long id);

    @Query(
        "SELECT COUNT(r) FROM Room r WHERE r.createdAt >= :startOfDay AND r.createdAt <= :endOfDay "
            + "AND r.roomStatus >= :startRoomStatusFlag AND r.roomStatus <= :endRoomStatusFlag")
    Integer countByCreatedAtBetweenAndRoomStatus(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay,
        @Param("startRoomStatusFlag") Integer startRoomStatusFlag,
        @Param("endRoomStatusFlag") Integer endRoomStatusFlag);
}
