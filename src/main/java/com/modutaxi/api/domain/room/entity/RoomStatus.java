package com.modutaxi.api.domain.room.entity;

public enum RoomStatus {
    PROCEEDING("모집 중"),
    COMPLETE("완료"),
    DELETE("Deleted Room"),
    ;

    private final String status;

    RoomStatus(String status) {
        this.status = status;
    }
}
