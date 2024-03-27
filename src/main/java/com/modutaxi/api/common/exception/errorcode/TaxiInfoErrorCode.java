package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TaxiInfoErrorCode implements ErrorCode {
    SAME_ORIGIN_DESTINATION("TAXI_INFO_001", "출발지와 도착지가 동일합니다.", HttpStatus.BAD_REQUEST),
    NOT_AROUND_ROAD("TAXI_INFO_002", "출발지 또는 도착지가 도로 주변이 아닙니다", HttpStatus.BAD_REQUEST),
    FAIL_FIND_ROUTE("TAXI_INFO_003", "자동차 길찾기 결과 제공 불가합니다.", HttpStatus.CONFLICT),
    STOPOVER_NOT_AROUND_ROAD("TAXI_INFO_004", "경유지가 도로 주변이 아닙니다.", HttpStatus.BAD_REQUEST),
    TOO_LONG_PATH("TAXI_INFO_005", "요청 경로가 매우 깁니다.", HttpStatus.BAD_REQUEST);

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}