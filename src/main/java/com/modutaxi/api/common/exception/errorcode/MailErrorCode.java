package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MailErrorCode implements ErrorCode {
    SES_SERVER_ERROR("MAIL_001", "", HttpStatus.BAD_REQUEST),
    MESSAGING_ERROR("MAIL_002", "메세지 발송에 실패했습니다.", HttpStatus.BAD_REQUEST),
    INVALID_EMAIL_FORM("MAIL_003", "유효하지 않은 이메일 형식입니다.", HttpStatus.BAD_REQUEST),
    UNSUPPOERTED_DOMAIN("MAIL_004", "지원하지 않는 도메인입니다.", HttpStatus.BAD_REQUEST),
    USED_EMAIL("MAIL_005", "이미 사용중인 이메일입니다.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_CODE_EXPIRED("MAIL_006", "인증 코드가 만료되었거나 존재하지 않습니다.", HttpStatus.BAD_REQUEST),
    CERTIFICATION_CODE_NOT_MATCH("MAIL_007", "인증 코드가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    TOO_MANY_CERTIFICATION_CODE_REQUEST("MAIL_008", "이미 발송된 인증메일 요청입니다.", HttpStatus.TOO_MANY_REQUESTS),
    ALREADY_CERTIFIED_EMAIL("MAIL_009", "이미 인증된 계정입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
