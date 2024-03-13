package com.modutaxi.api.domain.member;

public enum Role {

    ROLE_VISITOR("일반 회원"),
    ROLE_MEMBER("인증 회원"),
    ROLE_MANAGER("관리자");

    private final String name;

    Role(String name) {
        this.name = name;
    }
}
