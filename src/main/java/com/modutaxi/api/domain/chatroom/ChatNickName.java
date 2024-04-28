package com.modutaxi.api.domain.chatroom;

public enum ChatNickName {
    MASTER,
    FIRST,
    SECOND,
    THIRD
    ;

    private int value;

    ChatNickName() {
        this.value = 1 << this.ordinal();
    }

    public int getValue() {
        return value;
    }
}
