package com.modutaxi.api.domain.scheduledmessage.mapper;

import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessage;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessageStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ScheduledMessageMapper {
    public static ScheduledMessage toEntity(Long roomId, String content, LocalDateTime dateTime){
        return ScheduledMessage.builder()
            .roomId(roomId)
            .content(content)
            .executeTime(dateTime)
            .status(ScheduledMessageStatus.PENDING)
            .build();
    }
}
