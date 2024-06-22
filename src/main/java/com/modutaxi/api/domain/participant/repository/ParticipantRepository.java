package com.modutaxi.api.domain.participant.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.participant.entity.Participant;
import com.modutaxi.api.domain.room.entity.Room;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByMemberId(Long memberId);

    List<Participant> findAllByRoom(Room room);

    List<Participant> findAllByRoomId(Long roomId);

    void deleteParticipantByMemberAndRoom(Member member, Room room);

    void deleteAllByRoom(Room room);

    boolean existsByMemberAndRoom(Member member, Room room);
}
