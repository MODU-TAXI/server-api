package com.modutaxi.api.domain.alarm.dto;

import com.modutaxi.api.domain.alarm.entity.AlarmType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class AlarmResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class AlarmInfo {
        private AlarmType type;
        private String message;
        private Long resourceId;
        private LocalDateTime dateTime;
        private boolean isChecked;
    }
}
