package com.modutaxi.api.domain.paymentmember.repository;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMember;
import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentMemberRepository extends JpaRepository<PaymentMember, Long> {

    List<PaymentMember> findAllByPaymentRoom(PaymentRoom paymentRoom);

    Optional<PaymentMember> findByPaymentRoomAndMember(PaymentRoom paymentRoom, Member member);

    @Query("SELECT COUNT(pm) FROM PaymentMember pm WHERE pm.status = :status AND pm.paymentRoom = :paymentRoom")
    int countByPaymentRoomAndStatus(@Param("paymentRoom") PaymentRoom paymentRoom,
        @Param("status") PaymentMemberStatus status);
}
