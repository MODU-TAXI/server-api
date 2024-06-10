package com.modutaxi.api.domain.paymentmember.service;

import static com.modutaxi.api.domain.paymentmember.mapper.PaymentMemberMapper.toDto;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentmember.repository.PaymentMemberRepository;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterPaymentMemberService {

    private final PaymentMemberRepository paymentMemberRepository;

    public void register(PaymentRoom paymentRoom, Member member, PaymentMemberStatus status) {
        PaymentMember paymentMember = toDto(paymentRoom, member, status);
        paymentMemberRepository.save(paymentMember);
    }
}
