package com.modutaxi.api.domain.paymentroom.repository;

import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRoomRepository extends JpaRepository<PaymentRoom, Long> {

    Optional<PaymentRoom> findByRoomId(Long roomId);
}
