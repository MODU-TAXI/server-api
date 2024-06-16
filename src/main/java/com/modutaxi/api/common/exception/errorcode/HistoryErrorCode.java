package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum HistoryErrorCode implements ErrorCode {
    EMPTY_HISTORY("HISTORY_001", "존재하지 않는 기록입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}