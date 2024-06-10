package com.modutaxi.api.domain.paymentmember.repository;

import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMemberRepository extends JpaRepository<PaymentMember, Long> {
}
