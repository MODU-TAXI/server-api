package com.modutaxi.api.domain.likedSpot.repository;

import com.modutaxi.api.domain.likedSpot.entity.LikedSpot;
import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikedSpotRepository extends JpaRepository<LikedSpot, Long> {
    Boolean existsByMemberAndSpotId(Member member, Long spotId);
}
