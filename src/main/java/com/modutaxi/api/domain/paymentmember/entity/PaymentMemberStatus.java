package com.modutaxi.api.domain.paymentmember.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMemberStatus {

    COMPLETE("정산 완료"),
    INCOMPLETE("정산 미완료"),
    DEACTIVATED("정산 대상 미포함");

    private final String description;
}
