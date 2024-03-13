package com.modutaxi.api.common.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ExceptionResponse {
    private final String code;
    private final String message;
    private final LocalDateTime timeStamp;

    public ExceptionResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.timeStamp = LocalDateTime.now();
    }
}
