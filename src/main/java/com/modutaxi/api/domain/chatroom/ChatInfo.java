package com.modutaxi.api.domain.chatroom;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatInfo implements Serializable {
    private String roomId;
    private String nickname;
}
