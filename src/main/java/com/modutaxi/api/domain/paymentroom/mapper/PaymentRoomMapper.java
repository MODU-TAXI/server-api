package com.modutaxi.api.domain.paymentroom.mapper;

import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.PaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;

public class PaymentRoomMapper {

    public static PaymentRoom toEntity(Long roomId, int totalCharge, Long accountId) {
        return PaymentRoom.builder()
            .roomId(roomId)
            .totalCharge(totalCharge)
            .accountId(accountId)
            .build();
    }

    public static PaymentRoomResponse toDto(PaymentRoom paymentRoom, Account account) {
        return PaymentRoomResponse.builder()
            .accountNumber(account.getAccountNumber())
            .bank(account.getBank())
            .totalCharge(paymentRoom.getTotalCharge())
            .status(paymentRoom.getStatus())
            .build();
    }
}
