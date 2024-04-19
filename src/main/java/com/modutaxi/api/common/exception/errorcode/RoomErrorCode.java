package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum RoomErrorCode implements ErrorCode {
    EMPTY_ROOM("ROOM_001", "존재하지 않는 방입니다.", HttpStatus.CONFLICT),
    NOT_ROOM_MANAGER("ROOM_002", "권한이 없는 사용자입니다.", HttpStatus.CONFLICT),
    ALREADY_MEMBER_IS_MANAGER("ROOM_003", "방을 두 개 이상 만들 수 없습니다.", HttpStatus.CONFLICT),
    BOTH_GENDER("ROOM_004", "성별 제한이 둘 다 설정되어 있습니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
