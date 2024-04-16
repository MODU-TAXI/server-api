package com.modutaxi.api.domain.memberspot.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class MemberSpot extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public static MemberSpot toEntity(Spot spot, Member member){
        return MemberSpot.builder()
            .spot(spot)
            .member(member)
            .build();
    }
}
