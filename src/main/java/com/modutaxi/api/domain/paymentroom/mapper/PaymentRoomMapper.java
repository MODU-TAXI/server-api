package com.modutaxi.api.domain.paymentroom.mapper;

import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;

public class PaymentRoomMapper {

    public static PaymentRoom toEntity(Long roomId, int totalCharge, Account account) {
        return PaymentRoom.builder()
            .roomId(roomId)
            .totalCharge(totalCharge)
            .account(account)
            .build();
    }
}
