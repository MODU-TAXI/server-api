package com.modutaxi.api.domain.scheduledmessage.repository;

import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessage;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledMessageRepository extends JpaRepository<ScheduledMessage, Long> {
    List<ScheduledMessage> findAllByStatus(ScheduledMessageStatus status);
}