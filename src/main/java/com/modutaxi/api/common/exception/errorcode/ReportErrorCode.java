package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements ErrorCode {
    SHORT_CONTENT("REPORT_001", "신고 사유는 10자 이상 입력해주세요.", HttpStatus.BAD_REQUEST),
    SELF_REPORT("REPORT_002", "자신은 신고할 수 없습니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
