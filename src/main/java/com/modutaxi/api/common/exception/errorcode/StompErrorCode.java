package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.SocketErrorCode;
import lombok.Getter;

@Getter
public enum StompErrorCode implements SocketErrorCode {
    FULL_CHAT_ROOM("SOCKET_001"),
    FAULT_ROOM_ID("SOCKET_002"),
    ALREADY_ROOM_IN("SOCKET_003"),
    ;
    private final String message;

    StompErrorCode(String message) {
        this.message = message;
    }

}