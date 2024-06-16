package com.modutaxi.api.common.s3;

public enum S3ObjectType {
    PROFILE("프로필 사진"),
    MESSAGE("메세지"),
    EXT("기타"),
    ;

    private final String info;

    S3ObjectType(String info) {
        this.info = info;
    }
}
