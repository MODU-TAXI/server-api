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
    BOTH_GENDER("ROOM_004", "성별 제한이 둘 다 설정되어 있습니다.", HttpStatus.BAD_REQUEST),
    DEPARTURE_BEFORE_CURRENT("ROOM_005", "출발 시간은 현재 시간보다 이전일 수 없습니다.", HttpStatus.BAD_REQUEST),
    DEPARTURE_EXCEED_RANGE("ROOM_006", "출발 위치는 대한민국을 벗어날 수 없습니다.", HttpStatus.BAD_REQUEST),
    POINT_IS_NOT_INDEPENDENT("ROOM_007", "위도와 경도는 한 묶음", HttpStatus.CONFLICT),
    MANAGER_CAN_ONLY_DELETE("ROOM_008", "룸 매니저는 퇴장할 수 없습니다. 룸 매니저는 오로지 삭제만 가능합니다.", HttpStatus.BAD_REQUEST),
    ALREADY_IN_ROOM("ROOM_009", "이미 방에 참가하고 있습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_MATCHING_COMPLETE("ROOM_010", "해당 방의 매칭은 이미 완료되었습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String errorCode;
    private final String message;
    private final HttpStatus status;
}
