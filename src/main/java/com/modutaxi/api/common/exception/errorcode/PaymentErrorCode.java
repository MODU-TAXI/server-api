package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PaymentErrorCode implements ErrorCode {
    INVALID_ACCOUNT("PAYMENT_001", "존재하지 않는 계좌입니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
