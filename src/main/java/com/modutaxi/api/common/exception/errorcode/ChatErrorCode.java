package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ChatErrorCode implements ErrorCode {

    INVALID_FCM_TOKEN("CHAT_001", "유효하지 않은 FCM 토큰입니다.", HttpStatus.CONFLICT),
    FAIL_FCM_SUBSCRIBE("CHAT_002", "FCM 구독에 실패했습니다.", HttpStatus.CONFLICT),
    FAIL_SEND_MESSAGE("CHAT_003", "메세지 전송에 실패했습니다", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;

}
