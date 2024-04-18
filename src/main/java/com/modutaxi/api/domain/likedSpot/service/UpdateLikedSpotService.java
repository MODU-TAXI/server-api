package com.modutaxi.api.domain.likedSpot.service;

import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotResponse;
import com.modutaxi.api.domain.likedSpot.repository.LikedSpotRepository;
import com.modutaxi.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UpdateLikedSpotService {
    private final LikedSpotRepository likedSpotRepository;

    @Transactional
    public LikedSpotResponse deleteLikedSpot(Member member, Long spotId) {
        likedSpotRepository.deleteByMemberAndSpotId(member, spotId);
        return new LikedSpotResponse(true);
    }
}
