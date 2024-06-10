package com.modutaxi.api.domain.paymentmember.mapper;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;

public class PaymentMemberMapper {

    public static PaymentMember toDto(PaymentRoom paymentRoom, Member member,
        PaymentMemberStatus status) {
        return PaymentMember.builder()
            .paymentRoom(paymentRoom)
            .member(member)
            .status(status)
            .build();
    }
}
