package com.modutaxi.api.domain.alarm.repository;

import com.modutaxi.api.domain.alarm.entity.Alarm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {

    Page<Alarm> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    void deleteByMemberId(Long memberId);
}
