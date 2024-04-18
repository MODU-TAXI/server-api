package com.modutaxi.api.domain.likedSpot.entity;

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
public class LikedSpot extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Spot spot;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public static LikedSpot toEntity(Spot spot, Member member){
        return LikedSpot.builder()
            .spot(spot)
            .member(member)
            .build();
    }
}
