package com.modutaxi.api.domain.likedSpot.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotResponse;
import com.modutaxi.api.domain.likedSpot.service.UpdateLikedSpotService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots/liked")
@Tag(name = "즐겨찾기 거점 제거", description = "즐겨찾기 거점 제거 API")
public class UpdateLikedSpotController {
    private final UpdateLikedSpotService updateLikedSpotService;

    @DeleteMapping("/{spotId}")
    @Operation(summary = "즐겨찾기 거점 제거", description = "특정 거점의 좋아요를 제거 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "즐겨찾기 거점 제거 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikedSpotResponse.class))),
    })
    public ResponseEntity<LikedSpotResponse> delelteLikedSpot(@CurrentMember Member member, @Parameter(description = "거점 id") @PathVariable("spotId") Long spotId) {
        return ResponseEntity.ok(updateLikedSpotService.deleteLikedSpot(member, spotId));
    }
}
