package com.modutaxi.api.domain.paymentmember.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.paymentroom.entity.PaymentRoom;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "payment_member", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "payment_room_id"})
})
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentMember extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private PaymentRoom paymentRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @NotNull
    private PaymentMemberStatus status;

    public void setStatusComplete() {
        this.status = PaymentMemberStatus.COMPLETE;
    }
}
