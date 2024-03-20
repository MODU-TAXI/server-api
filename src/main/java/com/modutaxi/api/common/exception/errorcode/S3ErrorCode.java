package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    UPLOAD_ERROR("S3_001", "업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
