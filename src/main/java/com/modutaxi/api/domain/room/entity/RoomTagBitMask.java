package com.modutaxi.api.domain.room.entity;

public enum RoomTagBitMask {
    ONLY_WOMAN(1),
    ONLY_MAN(2),
    REGARDLESS_OF_GENDER(4),

    STUDENT_CERTIFICATION(8);



    private int value;

    RoomTagBitMask(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

}
