package com.modutaxi.api.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    private final String errorCode;
    private String message;
    private final HttpStatus status;

    public BaseException(ErrorCode code) {
        this.errorCode = code.getErrorCode();
        this.message = code.getMessage();
        this.status = code.getStatus();
    }

    public BaseException(ErrorCode code, String message) {
        this.errorCode = code.getErrorCode();
        this.message = message;
        this.status = code.getStatus();
    }

}
