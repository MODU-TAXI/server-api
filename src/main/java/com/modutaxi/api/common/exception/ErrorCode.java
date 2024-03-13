package com.modutaxi.api.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String getErrorCode();
    String getMessage();
    HttpStatus getStatus();
}
