package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum S3ErrorCode implements ErrorCode {

    UPLOAD_ERROR("S3_001", "업로드에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NOT_EXIST_FILE("S3_002", "삭제할 파일이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    OBJECT_TYPE_ERROR("S3_003", "잘못된 객체 타입입니다.", HttpStatus.UNAUTHORIZED),
    AWS_CONNECTION_ERROR("S3_004", "AWS 연결에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
