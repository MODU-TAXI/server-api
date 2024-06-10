package com.modutaxi.api.domain.paymentroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PaymentRoomResponseDto {

    @Getter
    @AllArgsConstructor
    public static class RegisterPaymentRoomResponse {
        private Long paymentRoomId;
    }
}
