package com.modutaxi.api.domain.paymentroom.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.account.entity.Account;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentRoom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Long roomId;
    
    @Builder.Default
    private PaymentStatus status = PaymentStatus.INCOMPLETE;

    @NotNull
    private int totalCharge;

    @OneToOne
    private Account account;
}
