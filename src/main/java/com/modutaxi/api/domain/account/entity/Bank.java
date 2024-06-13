package com.modutaxi.api.domain.account.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Bank {
    NH("NH농협"),
    KAKAO("카카오뱅크"),
    KB("KB국민"),
    TOSS("토스뱅크"),
    SHINHAN("신한"),
    WOORI("우리"),
    IBK("IBK기업"),
    HANA("하나"),
    MG("새마을"),
    BUSAN("부산"),
    DAEGU("대구"),
    K("케이뱅크"),
    SHINHYUP("신협"),
    POST("우체국"),
    SC("SC제일"),
    BNK("경남"),
    GWANGJU("광주"),
    SUHYUP("수협"),
    JEONBUK("전북"),
    SB("저축은행"),
    JEJU("제주");

    private final String name;
}
