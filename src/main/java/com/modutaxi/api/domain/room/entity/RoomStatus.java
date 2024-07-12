package com.modutaxi.api.domain.room.entity;

public enum RoomStatus {
    BEFORE_MATCHING("매칭 중"),
    AFTER_MATCHING("매칭 완료"),
    BEFORE_PAYMENT("정산 중"),
    AFTER_PAYMENT("정산 완료"),
    DELETE("삭제 완료"),
    ;

    private final String status;

    RoomStatus(String status) {
        this.status = status;
    }
}
