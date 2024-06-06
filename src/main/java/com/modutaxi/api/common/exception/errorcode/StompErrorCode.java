package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.ErrorCode;
import com.modutaxi.api.common.exception.SocketErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StompErrorCode implements SocketErrorCode {
    FULL_CHAT_ROOM("SOCKET_001"),
    ROOM_ID_IS_NULL("SOCKET_002"),
    FAULT_ROOM_ID("SOCKET_003"),
    ALREADY_ROOM_IN("SOCKET_004"),
    FAULT_JWT("SOCKET_005"),
    FAIL_SEND_MESSAGE("SOCKET_006"),
    EMPTY_MEMBER("SOCKET_007"),
    EMPTY_JWT("AUTH_001"),
    INVALID_JWT("AUTH_002"),
    EXPIRED_MEMBER_JWT("AUTH_003"),
    UNSUPPORTED_JWT("AUTH_004"),
    INVALID_SNS_ID_KEY("AUTH_005"),
    INVALID_ACCESS_TOKEN("AUTH_006"),
    FAILED_SOCIAL_LOGIN("AUTH_007"),
    LOGOUT_JWT("AUTH_008"),
    ;
    private final String ErrorCode;

    StompErrorCode(String ErrorCode) {
        this.ErrorCode = ErrorCode;
    }

}
