package com.modutaxi.api.domain.alarm.mapper;

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
}
