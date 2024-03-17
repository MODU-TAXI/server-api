package com.modutaxi.api.domain.participant.repository;

import com.modutaxi.api.domain.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
