package com.modutaxi.api.domain.alarm.mapper;

import com.modutaxi.api.domain.alarm.dto.AlarmResponseDto.AlarmInfo;
import com.modutaxi.api.domain.alarm.entity.Alarm;
import com.modutaxi.api.domain.alarm.entity.AlarmType;
import org.springframework.stereotype.Component;

@Component
public class AlarmMapper {

    public static Alarm toEntity(AlarmType type, Long resourceId, Long memberId) {
        return Alarm.builder()
            .type(type)
            .resourceId(resourceId)
            .memberId(memberId)
            .build();
    }

    public static AlarmInfo toDto(Alarm alarm) {
        return AlarmInfo.builder()
            .type(alarm.getType())
            .message(alarm.getType().getMessage())
            .resourceId(alarm.getResourceId())
            .dateTime(alarm.getCreatedAt())
            .checked(alarm.isChecked())
            .build();
    }
}
