package com.modutaxi.api.domain.paymentmember.mapper;

import static com.modutaxi.api.common.util.string.StringConverter.convertNameMosaic;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.dto.PaymentMemberResponseDto.PaymentMemberResponse;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import java.util.Objects;

public class PaymentMemberMapper {

    public static PaymentMember toEntity(PaymentRoom paymentRoom, Member member,
        PaymentMemberStatus status) {
        return PaymentMember.builder()
            .paymentRoom(paymentRoom)
            .member(member)
            .status(status)
            .build();
    }

    public static PaymentMemberResponse toDto(PaymentMember paymentMember, Long memberId) {
        Member member = paymentMember.getMember();
        return PaymentMemberResponse.builder()
            .id(member.getId())
            .nickName(member.getNickname())
            .name("(" + convertNameMosaic(member.getName()) + ")")
            .imageUrl(member.getImageUrl())
            .status(paymentMember.getStatus())
            .isMe(Objects.equals(memberId, member.getId()))
            .build();
    }
}
