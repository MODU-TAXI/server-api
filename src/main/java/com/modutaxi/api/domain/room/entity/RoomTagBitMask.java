package com.modutaxi.api.domain.room.entity;

public enum RoomTagBitMask {
    ONLY_WOMAN,
    ONLY_MAN,
    MANNER,
    QUIET,
    STUDENT_CERTIFICATION
    ;


    private int value;

    RoomTagBitMask() {
        this.value = 1 << this.ordinal();
    }

    public int getValue() {
        return value;
    }

}
