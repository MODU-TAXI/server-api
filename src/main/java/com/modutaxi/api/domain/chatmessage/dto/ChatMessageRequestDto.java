package com.modutaxi.api.domain.chatmessage.dto;

import com.modutaxi.api.domain.chatmessage.entity.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageRequestDto {
    private Long roomId;
    private MessageType type;
    private String content;
    private String sender;
    private String token;
    // TODO: 4/25/24 token은 임시변수. dto에서 지우고 헤더로 들어가야함.
}
