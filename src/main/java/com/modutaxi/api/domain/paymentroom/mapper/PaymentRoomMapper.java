package com.modutaxi.api.domain.paymentroom.mapper;

import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.paymentroom.dto.PaymentRoomResponseDto.PaymentRoomResponse;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;

public class PaymentRoomMapper {

    public static PaymentRoom toEntity(Long roomId, int totalCharge, Account account) {
        return PaymentRoom.builder()
            .roomId(roomId)
            .totalCharge(totalCharge)
            .account(account)
            .build();
    }

    public static PaymentRoomResponse toDto(PaymentRoom paymentRoom) {
        return PaymentRoomResponse.builder()
            .paymentRoomId(paymentRoom.getId())
            .accountNumber(paymentRoom.getAccount().getAccountNumber())
            .bank(paymentRoom.getAccount().getBank())
            .totalCharge(paymentRoom.getTotalCharge())
            .status(paymentRoom.getStatus())
            .build();
    }
}
