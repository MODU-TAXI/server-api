package com.modutaxi.api.domain.room.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    @Modifying
    @Query(value = "UPDATE Room r SET r.roomManager =: memberId WHERE r.id = :roomId")
    void updateMemberId(@Param("roomId") Long roomId, @Param("memberId") Long memberId);
}
