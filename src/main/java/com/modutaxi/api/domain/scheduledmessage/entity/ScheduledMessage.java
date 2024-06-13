package com.modutaxi.api.domain.scheduledmessage.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
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

    public void scheduledMessageStatusUpdate() {
        this.status = ScheduledMessageStatus.COMPLETE;
    }
}
