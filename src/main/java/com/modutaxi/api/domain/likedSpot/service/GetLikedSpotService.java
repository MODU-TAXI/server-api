package com.modutaxi.api.domain.likedSpot.service;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.likedSpot.dao.LikedSpotMysqlResponse.LikedSpotResponseInterface;
import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotListResponse;
import com.modutaxi.api.domain.likedSpot.mapper.LikedSpotResponseMapper;
import com.modutaxi.api.domain.likedSpot.repository.LikedSpotRepository;
import com.modutaxi.api.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GetLikedSpotService {
    private final LikedSpotRepository likedSpotRepository;

    public PageResponseDto<List<LikedSpotListResponse>> getLikedSpotList(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size);
        Slice<LikedSpotResponseInterface> likedSpotSlice = likedSpotRepository.findAllByOrderByCreatedAtDesc(pageable, member);
        List<LikedSpotListResponse> likedSpotListResponseList = likedSpotSlice.stream()
                .map(LikedSpotResponseMapper::toDto)
                .toList();
        return new PageResponseDto<>(pageable.getPageNumber(), likedSpotSlice.hasNext(),
                likedSpotListResponseList);
    }
}
