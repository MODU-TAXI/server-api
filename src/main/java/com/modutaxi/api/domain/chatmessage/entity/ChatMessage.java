package com.modutaxi.api.domain.chatmessage.entity;

import com.modutaxi.api.common.entity.BaseTime;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ChatMessage extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @NotNull
    private Long roomId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MessageType messageType  = MessageType.CHAT;

    @NotNull
    @Builder.Default
    private String content = "";

    @NotNull
    @Builder.Default
    private String sender = "모두의택시";

    @NotNull
    @Builder.Default
    private Long memberId = 1L;

    @NotNull
    @Builder.Default
    private LocalDateTime dateTime = LocalDateTime.now();

}