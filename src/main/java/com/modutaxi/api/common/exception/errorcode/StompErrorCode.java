package com.modutaxi.api.common.exception.errorcode;


import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StompErrorCode implements ErrorCode {
    //방 관련
    FULL_CHAT_ROOM("SOCK_ROOM_001", "방이 꽉 차있습니다.", HttpStatus.BAD_REQUEST),
    ROOM_ID_IS_NULL("SOCK_ROOM_002", "방 ID 가 null입니다.", HttpStatus.BAD_REQUEST),
    FAULT_ROOM_ID("SOCK_ROOM_003", "잘못된 방 번호입니다.", HttpStatus.BAD_REQUEST),
    ALREADY_ROOM_IN("SOCK_ROOM_004", "당신은 이미 방에 참여한 상태입니다.", HttpStatus.BAD_REQUEST),


    //pub/sub 관련
    FAIL_SEND_MESSAGE("SOCK_MESSAGE_005","메세지 전송에 실패하였습니다.", HttpStatus.CONFLICT),

    //멤버 관련
    EMPTY_MEMBER("SOCK_MEMBER_006","존재하지 않는 멤버입니다.", HttpStatus.BAD_REQUEST),

    //login 관련
    EMPTY_JWT("SOCK_AUTH_007", "JWT가 없습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_JWT("SOCK_AUTH_008", "유효하지 않은 JWT입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_MEMBER_JWT("SOCK_AUTH_009", "만료된 JWT입니다.", HttpStatus.GONE),
    UNSUPPORTED_JWT("SOCK_AUTH_010", "지원하지 않는 JWT입니다.", HttpStatus.UNAUTHORIZED),
    INVALID_SNS_ID_KEY("SOCK_AUTH_011", "유효하지 않은 snsId key입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN("SOCK_AUTH_006", "유효하지 않은 ACCESS TOKEN입니다.", HttpStatus.BAD_REQUEST),
    FAILED_SOCIAL_LOGIN("SOCK_AUTH_007", "소셜 로그인에 실패하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    LOGOUT_JWT("SOCK_AUTH_008", "로그아웃 처리된 JWT입니다.", HttpStatus.UNAUTHORIZED);
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;

}
