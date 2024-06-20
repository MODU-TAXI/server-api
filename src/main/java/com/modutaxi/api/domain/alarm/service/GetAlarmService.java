package com.modutaxi.api.domain.alarm.service;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.alarm.dto.AlarmResponseDto.AlarmInfo;
import com.modutaxi.api.domain.alarm.entity.Alarm;
import com.modutaxi.api.domain.alarm.mapper.AlarmMapper;
import com.modutaxi.api.domain.alarm.repository.AlarmRepository;
import com.modutaxi.api.domain.member.entity.Member;
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

    public PageResponseDto<List<AlarmInfo>> getAlarmList(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Alarm> alarms = alarmRepository.findByMemberIdOrderByCreatedAtDesc(member.getId(),
            pageable);
        List<AlarmInfo> alarmInfos = alarms.stream().map(AlarmMapper::toDto).toList();
        return new PageResponseDto<>(page, alarms.hasNext(), alarmInfos);
    }
}