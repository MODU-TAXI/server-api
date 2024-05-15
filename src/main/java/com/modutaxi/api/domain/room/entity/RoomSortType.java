package com.modutaxi.api.domain.room.entity;

public enum RoomSortType {
    NEW("최신순"),
    DISTANCE("거리순"),
    ENDTIME("마감순"),
    ;

    private final String status;

    RoomSortType(String status) {
        this.status = status;
    }
}
