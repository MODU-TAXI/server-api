package com.modutaxi.api.domain.likedSpot.controller;

import com.modutaxi.api.common.pagination.PageResponseDto;
import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotListResponse;
import com.modutaxi.api.domain.likedSpot.service.GetLikedSpotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots/liked")
@Tag(name = "즐겨찾기 거점 조회", description = "즐겨찾기 거점 조회 API")
public class GetLikedSpotController {
    private final GetLikedSpotService getLikedSpotService;

    @GetMapping
    @Operation(summary = "즐겨찾기 거점 목록 조회", description = "좋아요한 거점 목록을 조회합니다.<br>page와 size를 입력해주세요.<br>좋아요 id, 거점 id, 거점명, 거점 좌표를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "즐겨찾기 거점 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikedSpotListResponse.class))),
    })
    public ResponseEntity<PageResponseDto<List<LikedSpotListResponse>>> getLikedSpotList(@Parameter(description = "조회할 page") @RequestParam int page, @Parameter(description = "조회할 page 단위") @RequestParam int size) {
        return ResponseEntity.ok(getLikedSpotService.getLikedSpotList(page, size));
    }
}
