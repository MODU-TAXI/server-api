package com.modutaxi.api.domain.likedSpot.repository;

import com.modutaxi.api.domain.likedSpot.dao.LikedSpotMysqlResponse.LikedSpotResponseInterface;
import com.modutaxi.api.domain.likedSpot.entity.LikedSpot;
import com.modutaxi.api.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LikedSpotRepository extends JpaRepository<LikedSpot, Long> {
    Boolean existsByMemberAndSpotId(Member member, Long spotId);

    void deleteByMemberAndSpotId(Member memberId, Long spotId);

    @Query("SELECT l.spot.id as spotId, l.spot.name as spotName, l.spot.spotPoint as spotpoint FROM LikedSpot l")
    Slice<LikedSpotResponseInterface> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
