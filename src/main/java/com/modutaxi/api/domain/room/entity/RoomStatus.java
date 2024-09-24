package com.modutaxi.api.domain.room.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum RoomStatus {
    BEFORE_MATCHING("매칭 중", 0),
    AFTER_MATCHING("매칭 완료", 1),
    BEFORE_PAYMENT("정산 중", 2),
    AFTER_PAYMENT("정산 완료", 3),
    DELETE("삭제 완료", 4),
    ;

    private final String status;
    private final Integer index;

}
