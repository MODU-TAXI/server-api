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
    INVALID_FCM_TOKEN("CHAT_005", "유효하지 않은 FCM 토큰입니다.", HttpStatus.CONFLICT),
    FAIL_FCM_SUBSCRIBE("CHAT_006", "FCM 구독에 실패했습니다.", HttpStatus.CONFLICT),
    FAIL_SEND_MESSAGE("CHAT_007", "메세지 전송에 실패했습니다", HttpStatus.CONFLICT),
    FAIL_FCM_UNSUBSCRIBE("CHAT_008", "FCM 구독취소에 실패했습니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
