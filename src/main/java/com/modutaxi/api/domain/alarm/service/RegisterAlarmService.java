package com.modutaxi.api.domain.alarm.service;

import static com.modutaxi.api.domain.alarm.mapper.AlarmMapper.toEntity;

import com.modutaxi.api.domain.alarm.entity.Alarm;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import com.modutaxi.api.domain.alarm.repository.AlarmRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public void registerAlarm(AlarmType type, Long resourceId, Long memberId) {
        Alarm alarm =  toEntity(type, resourceId, memberId);
        alarmRepository.save(alarm);
    }
}
