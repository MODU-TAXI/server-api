package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SmsErrorCode implements ErrorCode {
    CERTIFICATION_CODE_EXPIRED("SMS_001", "인증번호가 만료되었거나 없습니다.", HttpStatus.BAD_REQUEST),
    PHONE_NUMBER_NOT_MATCH("SMS_002", "인증번호를 요청한 번호와 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_CODE_NOT_MATCH("SMS_003", "인증번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_CODE_ALREADY_SENT("SMS_004", "이미 인증번호가 발송되었습니다. 잠시후 재시도 해주세요.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_CODE_SENDING("SMS_005", "인증번호가 발송중입니다.", HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER_PATTERN("SMS_006", "유효하지 않은 전화번호 형식입니다.", HttpStatus.BAD_REQUEST),
    INVALID_CERTIFICATION_CODE_PATTERN("SMS_007", "유효하지 않은 인증코드 형식입니다.", HttpStatus.BAD_REQUEST),
    SMS_AGENCY_ERROR("SMS_008", "SMS 발송에 실패했습니다. 잠시후 재시도 해주세요.", HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
