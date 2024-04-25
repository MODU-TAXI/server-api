package com.modutaxi.api.domain.onboarding.controller;

import com.modutaxi.api.common.exception.errorcode.OnboardingErrorCode;
import com.modutaxi.api.domain.onboarding.dto.OnboardingRequestDto.OnboardingRequest;
import com.modutaxi.api.domain.onboarding.service.RegisterOnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/onboardings")
@Tag(name = "설문 조사", description = "설문 조사 API")
public class RegisterOnboardingController {

    private final RegisterOnboardingService registerOnboardingService;

    /**
     * [POST] 설문 조사
     */
    @Operation(summary = "설문 조사", description = """
            성공하면 body에 200 내려가요!
            현재 존재하는 설문조사 번호는 1~2번입니다.
            etc가 true일 때는 etcContent에 기타 사유를 넣어주시고, etc가 false라면 etcContent에 null이 아닌 블랭크를 담아 전송해주세요.""")
    @PostMapping("")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "설문 조사 성공"),
            @ApiResponse(responseCode = "400", description = "설문 조사 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = OnboardingErrorCode.class), examples = {
                    @ExampleObject(name = "ONBOARDING_001", value = """
                            {
                                "code": "ONBOARDING_001",
                                "message": "존재하지 않는 설문조사 ID 입니다.",
                                "timeStamp": "2024-04-04T02:48:31.646102"
                            }
                            """, description = "일치하는 ID를 가진 설문조사가 존재하지 않습니다."),
            })),
    })
    public ResponseEntity<Integer> register(
            @Valid @RequestBody OnboardingRequest onboardingRequest) {
        registerOnboardingService.registerOnboarding(onboardingRequest);
        return ResponseEntity.ok(200);
    }

}
