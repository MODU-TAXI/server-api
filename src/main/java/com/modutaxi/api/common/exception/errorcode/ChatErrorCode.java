package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    FULL_CHAT_ROOM("CHAT_001", "방이 꽉 차있습니다", HttpStatus.BAD_REQUEST),
    FAULT_ROOM_ID("CHAT_002", "잘못된 방입니다. 다시 확인하세요.", HttpStatus.BAD_REQUEST),
    ALREADY_ROOM_IN("CHAT_003", "당신은 이미 방에 참가해있습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ROOM_OUT("CHAT_004", "방에서 이미 나간 상태입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}

