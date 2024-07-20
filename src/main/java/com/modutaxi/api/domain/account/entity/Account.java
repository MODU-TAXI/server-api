package com.modutaxi.api.domain.account.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Account extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String accountNumber;
    @NotNull
    private Bank bank;
    @NotNull
    private String ownerName;

    @Builder.Default
    private boolean status = true;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
