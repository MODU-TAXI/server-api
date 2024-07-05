package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    EMPTY_JWT("AUTH_001", "JWT가 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("AUTH_002", "유효하지 않은 JWT입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_MEMBER_JWT("AUTH_003", "만료된 JWT입니다.", HttpStatus.GONE),
    UNSUPPORTED_JWT("AUTH_004", "지원하지 않는 JWT입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_SNS_ID_KEY("AUTH_005", "유효하지 않은 snsId key입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("AUTH_006", "유효하지 않은 ACCESS TOKEN입니다.", HttpStatus.BAD_REQUEST),
    FAILED_SOCIAL_LOGIN("AUTH_007", "소셜 로그인에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    LOGOUT_JWT("AUTH_008", "로그아웃 처리된 JWT입니다.", HttpStatus.UNAUTHORIZED),
    APPLE_LOGIN_ERROR("AUTH_009", "Apple 로그인에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_REVOKE_ERROR("AUTH_010", "Apple 탈퇴에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
