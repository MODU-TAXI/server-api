package com.modutaxi.api.domain.roomwaiting.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.room.entity.Room;
import com.modutaxi.api.domain.roomwaiting.entity.RoomWaiting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomWaitingRepository extends JpaRepository<RoomWaiting, Long> {
    List<RoomWaiting> findAllByRoomId(Long roomId);
    boolean existsByMemberAndRoom(Member member, Room room);

    void deleteByMemberAndRoom(Member member, Room room);
}
