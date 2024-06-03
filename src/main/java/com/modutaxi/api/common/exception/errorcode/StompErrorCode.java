package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.SocketErrorCode;
import lombok.Getter;

@Getter
public enum StompErrorCode implements SocketErrorCode {
    FULL_CHAT_ROOM("SOCKET_001"),
    ROOM_ID_IS_NULL("SOCKET_002"),
    FAULT_ROOM_ID("SOCKET_003"),
    ALREADY_ROOM_IN("SOCKET_004"),
    FAULT_JWT("SOCKET_005"),
    FAIL_SEND_MESSAGE("SOCKET_006"),
    ;
    private final String ErrorCode;

    StompErrorCode(String ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

}