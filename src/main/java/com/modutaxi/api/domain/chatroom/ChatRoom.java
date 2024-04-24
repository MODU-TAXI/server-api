package com.modutaxi.api.domain.chatroom;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoom {
    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;

    public static ChatRoom create(Long roomId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(roomId);
        return chatRoom;
    }
}