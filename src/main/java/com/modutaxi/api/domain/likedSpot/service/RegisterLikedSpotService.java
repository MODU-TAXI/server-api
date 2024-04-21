package com.modutaxi.api.domain.likedSpot.service;

import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotResponse;
import com.modutaxi.api.domain.likedSpot.entity.LikedSpot;
import com.modutaxi.api.domain.likedSpot.repository.LikedSpotRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.spot.entity.Spot;
import com.modutaxi.api.domain.spot.service.GetSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RegisterLikedSpotService {
    private final LikedSpotRepository likedSpotRepository;
    private final GetSpotService getSpotService;

    @Transactional
    public LikedSpotResponse registerLikedSpot(Member member, Long spotId) {
        if(likedSpotRepository.existsByMemberAndSpotId(member, spotId))
            return new LikedSpotResponse(false);
        likedSpotRepository.save(LikedSpot.toEntity(getSpotService.getSpot(spotId), member));
        return new LikedSpotResponse(true);
    }
}
