package com.modutaxi.api.domain.scheduledmessage.mapper;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessage;
import com.modutaxi.api.domain.scheduledmessage.entity.ScheduledMessageStatus;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ScheduledMessageMapper {
    public static ScheduledMessage toEntity(Long roomId, String content, LocalDateTime dateTime,
        MessageType type) {
        return ScheduledMessage.builder()
            .roomId(roomId)
            .content(content)
            .executeTime(dateTime)
            .status(ScheduledMessageStatus.PENDING)
            .type(type)
            .build();
    }
}
