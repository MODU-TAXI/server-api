package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCode {
    EMPTY_MEMBER("MEMBER_001", "존재하지 않는 사용자입니다.", HttpStatus.CONFLICT),
    DUPLICATE_MEMBER("MEMBER_002", "중복된 사용자입니다.", HttpStatus.CONFLICT),
    UN_REGISTERED_MEMBER("MEMBER_004", "", HttpStatus.CONFLICT),
    INVALID_SIGN_IN_KEY("MEMBER_005", "유효하지 않은 로그인 키입니다.", HttpStatus.UNAUTHORIZED),

    // 닉네임 관련, 경고 문구 그대로 사용
    DUPLICATE_NICKNAME("MEMBER_003", "이미 있는 닉네임이에요!", HttpStatus.CONFLICT),
    INVALID_NICKNAME("MEMBER_006", "한글, 영어, 숫자만 사용할 수 있어요!", HttpStatus.BAD_REQUEST),
    TOO_SHORT_NICKNAME("MEMBER_007", "닉네임은 최소 2글자부터 가능해요!", HttpStatus.BAD_REQUEST),
    TOO_LONG_NICKNAME("MEMBER_008", "닉네임은 최대 12글자까지 가능해요!", HttpStatus.BAD_REQUEST),
    INAPPROPRIATE_WORD_NICKNAME("MEMBER_009", "비속어 혹은 부적절한 단어가 포함된 닉네임은 생성할 수 없어요!",
        HttpStatus.BAD_REQUEST),

    BLOCKED_MEMBER("MEMBER_010", "임시 차단된 사용자입니다.", HttpStatus.UNAUTHORIZED),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
