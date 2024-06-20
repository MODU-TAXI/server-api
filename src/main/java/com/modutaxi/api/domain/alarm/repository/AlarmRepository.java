package com.modutaxi.api.domain.alarm.repository;

import com.modutaxi.api.domain.alarm.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
