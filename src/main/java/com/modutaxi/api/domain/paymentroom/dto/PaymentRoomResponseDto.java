package com.modutaxi.api.domain.paymentroom.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.paymentroom.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class PaymentRoomResponseDto {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class RegisterPaymentRoomResponse {
        private Long paymentRoomId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @ToString
    public static class PaymentRoomResponse {
        private String accountNumber;
        private Bank bank;
        private int totalCharge;
        private PaymentStatus status;
    }

}
