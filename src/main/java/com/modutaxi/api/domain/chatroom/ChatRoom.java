package com.modutaxi.api.domain.chatroom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoom {
    private String roomId;

    public static ChatRoom create(Long roomId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = String.valueOf(roomId);
        return chatRoom;
    }
}