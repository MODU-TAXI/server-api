package com.modutaxi.api.domain.chatmessage.dto;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChatMessageRequestDto {
    private Long roomId;
    private MessageType type;
    private String content;
    private String sender;
    private String memberId;
    private LocalDateTime dateTime;
    private String imageUrl;
}
