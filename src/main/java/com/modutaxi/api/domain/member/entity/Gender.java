package com.modutaxi.api.domain.member.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Gender {
    MALE("남성"), FEMALE("여성");

    private final String gender;
}
