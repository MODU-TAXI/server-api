package com.modutaxi.api.domain.paymentroom.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.paymentroom.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PaymentRoomResponseDto {

    @Getter
    @AllArgsConstructor
    public static class RegisterPaymentRoomResponse {
        private Long paymentRoomId;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaymentRoomResponse {
        private String accountNumber;
        private Bank bank;
        private int totalCharge;
        private PaymentStatus status;
    }

}
