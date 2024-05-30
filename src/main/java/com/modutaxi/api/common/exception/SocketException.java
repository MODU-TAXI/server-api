package com.modutaxi.api.common.exception;

import com.modutaxi.api.common.exception.errorcode.StompErrorCode;
import lombok.Getter;

@Getter
public class SocketException extends RuntimeException {
    private final StompErrorCode errorCode;

    public SocketException(StompErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public StompErrorCode getErrorCode() {
        return errorCode;
    }

}
