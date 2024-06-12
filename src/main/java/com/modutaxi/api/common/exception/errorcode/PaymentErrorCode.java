package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INVALID_ACCOUNT("PAYMENT_001", "존재하지 않는 계좌입니다.", HttpStatus.CONFLICT),
    INVALID_PAYMENT_ROOM("PAYMENT_002", "존재하지 않는 정산방입니다.", HttpStatus.CONFLICT),
    INVALID_PAYMENT_MEMBER("PAYMENT_003", "정산방에 존재하지 않는 멤버입니다.", HttpStatus.CONFLICT),
    ALREADY_PAYMENT_MEMBER("PAYMENT_004", "이미 정산한 멤버입니다.", HttpStatus.CONFLICT),
    NOT_PAYMENT_MEMBER("PAYMENT_005", "멤버가 정산 대상이 아닙니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
