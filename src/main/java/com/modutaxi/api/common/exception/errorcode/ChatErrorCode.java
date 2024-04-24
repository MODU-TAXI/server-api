package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    FULL_CHAT_ROOM("CHAT_001", "방이 꽉 차있습니다", HttpStatus.BAD_REQUEST),
    FAULT_ROOM_ID("CHAT_002", "잘못된 방", HttpStatus.BAD_REQUEST)
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}

