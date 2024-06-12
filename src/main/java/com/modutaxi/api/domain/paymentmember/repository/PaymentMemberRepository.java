package com.modutaxi.api.domain.paymentmember.repository;

import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentMemberRepository extends JpaRepository<PaymentMember, Long> {

    List<PaymentMember> findAllByPaymentRoom(PaymentRoom paymentRoom);
}
