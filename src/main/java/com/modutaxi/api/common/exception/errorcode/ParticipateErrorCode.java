package com.modutaxi.api.common.exception.errorcode;

import com.modutaxi.api.common.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ParticipateErrorCode implements ErrorCode {
    PARTICIPATE_NOT_ALLOW("PRT_001", "방이 참여가능한 상태가 아닙니다.", HttpStatus.BAD_REQUEST),
    YOUR_NOT_ROOM_MANAGER("PRT_002", "수락 권한이 없습니다.", HttpStatus.BAD_REQUEST),
    USER_NOT_IN_ROOM("PRT_003", "해당 사용자가 대기열에 없습니다.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_WAITING_LIST("PRT_004", "해당 사용자가 대기열에 이미 있습니다.", HttpStatus.BAD_REQUEST),
    USER_ALREADY_IN_ROOM("PRT_005", "해당 사용자가 이미 해당 채팅방에 존재합니다.", HttpStatus.BAD_REQUEST),
    ROOM_IS_FULL("PRT_006", "방의 정원이 꽉 찬 상태입니다.", HttpStatus.CONFLICT),
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
