package com.modutaxi.api.domain.memberdestination.entity;

import com.modutaxi.api.common.entity.BaseTime;
import com.modutaxi.api.domain.destination.entity.Destination;
import com.modutaxi.api.domain.member.Member;
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
public class MemberDestination extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Destination destination;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    public static MemberDestination toEntity(Destination destination, Member member){
        return MemberDestination.builder()
            .destination(destination)
            .member(member)
            .build();
    }
}
