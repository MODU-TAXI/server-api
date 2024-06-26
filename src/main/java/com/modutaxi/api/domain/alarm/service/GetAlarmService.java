package com.modutaxi.api.domain.alarm.service;

import static com.modutaxi.api.domain.alarm.mapper.AlarmMapper.toDto;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.alarm.dto.AlarmResponseDto.AlarmInfo;
import com.modutaxi.api.domain.alarm.entity.Alarm;
import com.modutaxi.api.domain.alarm.repository.AlarmRepository;
import com.modutaxi.api.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAlarmService {

    private final AlarmRepository alarmRepository;

    @Transactional
    public PageResponseDto<List<AlarmInfo>> getAlarmList(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Alarm> alarms = alarmRepository.findByMemberIdOrderByCreatedAtDesc(member.getId(),
            pageable);

        List<AlarmInfo> alarmInfos = new ArrayList<>();
        alarms.forEach(alarm -> {
                alarmInfos.add(toDto(alarm));
                alarm.setCheckedTrue();     // 모든 알림에 대해 읽음 처리
            }
        );
        return new PageResponseDto<>(page, alarms.hasNext(), alarmInfos);
    }
}