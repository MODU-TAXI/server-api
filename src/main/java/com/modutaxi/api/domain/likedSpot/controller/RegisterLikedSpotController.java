package com.modutaxi.api.domain.likedSpot.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.SpotError;
import com.modutaxi.api.domain.likedSpot.dto.LikedSpotResponseDto.LikedSpotResponse;
import com.modutaxi.api.domain.likedSpot.service.RegisterLikedSpotService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spots/liked")
@Tag(name = "즐겨찾기 거점 등록", description = "즐겨찾기 거점 등록 API")
public class RegisterLikedSpotController {
    private final RegisterLikedSpotService registerLikedSpotService;

    @PostMapping("/{spotId}")
    @Operation(summary = "즐겨찾기 거점 등록", description = "특정 거점을 좋아요 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "즐겨찾기 거점 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LikedSpotResponse.class))),
            @ApiResponse(responseCode = "400", description = "거점 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SpotError.class), examples = {
                    @ExampleObject(name = "SPOT_001", value = """
                            {
                                "code": "SPOT_001",
                                "message": "존재하지 않는 거점 ID 입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "일치하는 ID를 가진 거점이 존재하지 않습니다."),
            })),
    })
    public ResponseEntity<LikedSpotResponse> registerLikedSpot(@CurrentMember Member member, @Parameter(description = "거점 id") @PathVariable("spotId") Long spotId) {
        return ResponseEntity.ok(registerLikedSpotService.registerLikedSpot(member, spotId));
    }
}
