package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SpotError implements ErrorCode{
    SPOT_ID_NOT_FOUND("SPOT_001", "존재하지 않는 거점 ID 입니다.", HttpStatus.BAD_REQUEST),
    SPOT_COORDINATE_INVALID("SPOT_002", "거점 좌표가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    SPOT_POLYGON_INVALID("SPOT_003", "영역이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
    SPOT_NAME_DUPLICATED("SPOT_004", "사용중인 거점 이름입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
