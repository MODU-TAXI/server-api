package com.modutaxi.api.domain.room.enums;

public enum RoomStatus {
    PROCEEDING("모집 중"),
    COMPLETE("완료"),
    IMMINENT("마감 임박");

    private final String status;

    RoomStatus(String status) {
        this.status = status;
    }
}
