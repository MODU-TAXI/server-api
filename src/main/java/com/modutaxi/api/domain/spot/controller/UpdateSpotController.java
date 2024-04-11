package com.modutaxi.api.domain.spot.controller;


import com.modutaxi.api.domain.spot.dto.SpotResponseDto.DeleteSpotResponse;
import com.modutaxi.api.domain.spot.service.UpdateSpotService;
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
@RequestMapping("/api/spots")
@Tag(name = "거점 수정", description = "거점 수정 API")
public class UpdateSpotController {
    private final UpdateSpotService updateSpotService;

    @DeleteMapping("/{id}")
    @Operation(
            summary = "특정 거점 제거",
            description = "특정 id의 거점을 제거합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "거점 등록 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = DeleteSpotResponse.class))),
    })
    public ResponseEntity<DeleteSpotResponse> deleteSpot(
            @Parameter(description = "거점 id")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new DeleteSpotResponse(updateSpotService.deleteSpot(id)));
    }
}
