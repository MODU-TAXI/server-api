package com.modutaxi.api.domain.paymentroom.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {

    COMPLETE("정산 완료"),
    INCOMPLETE("정산 미완료");

    private final String description;
}
