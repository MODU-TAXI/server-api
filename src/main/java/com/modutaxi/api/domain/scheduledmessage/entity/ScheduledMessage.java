package com.modutaxi.api.domain.scheduledmessage.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
@ToString
public class ScheduledMessage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long roomId;

    @NotNull
    private String content;

    @NotNull
    private LocalDateTime executeTime;

    @NotNull
    private ScheduledMessageStatus status;

    @NotNull
    private MessageType type;

    public void scheduledMessageStatusUpdate() {
        this.status = ScheduledMessageStatus.COMPLETE;
    }
}
