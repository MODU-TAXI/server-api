package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.oauth.SocialLoginType;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.LoginRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SignUpRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.service.RegisterMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "회원 가입", description = "회원 가입 API")
public class RegisterMemberController {

    private final RegisterMemberService registerMemberService;

    /**
     * [POST] 소셜 회원가입
     * /sign-up
     */
    @Operation(summary = "소셜 회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(registerMemberService.registerMember(
                signUpRequest.getKey(), signUpRequest.getName(), signUpRequest.getGender()));
    }

    /**
     * [POST] 소셜 로그인
     * /{type}/login
     */
    @Operation(summary = "소셜 로그인")
    @PostMapping("/{type}/login")
    public ResponseEntity<TokenResponse> login(
            @PathVariable(name = "type") SocialLoginType type,
            @Valid @RequestBody LoginRequest loginRequest) throws IOException {
        return ResponseEntity.ok(registerMemberService.login(
                type, loginRequest.getAccessToken()));
    }

    /**
     * [GET] 이메일 인증 메일 발송
     * /mail-cert
     */
    @Operation(
        summary = "이메일 인증 메일 발송",
        description = "이메일 인증 메일을 발송합니다.<br>" +
            "로그인 실패 시 함께 반환받은 key 값을 이용하여 인증 메일을 발송합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메일 발송 성공"),
        @ApiResponse(responseCode = "400", description = "메일 발송 실패")
    })
    @GetMapping("/mail/cert")
    public ResponseEntity<Boolean> sendEmailCertificationMail(
        @Parameter(description = "로그인 실패 시 반환받은 key 값")
        @RequestParam String key,
        @Parameter(description = "인증 메일을 받을 이메일 주소")
        @RequestParam String receiver
    ) {
        return ResponseEntity.ok(registerMemberService.sendEmailCertificationMail(key, receiver));
    }

    /**
     * [GET] 이메일 인증 확인
     * /mail/confirm
     */
    @Operation(
        summary = "이메일 인증 확인",
        description = "수신한 인증 메일을 인증합니다.<br>" +
            "로그인 실패 시 함께 반환받은 key 값을 이용하여 인증코드를 인증합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메일 인증 성공"),
        @ApiResponse(responseCode = "400", description = "메일 인증 실패")
    })
    @GetMapping("/mail/confirm")
    public ResponseEntity<Boolean> confirmEmailCertification(
        @Parameter(description = "로그인 실패 시 반환받은 key 값")
        @RequestParam String key,
        @Parameter(description = "인증 메일로 받은 인증 코드")
        @RequestParam String code) {
        return ResponseEntity.ok(registerMemberService.checkEmailCertificationCode(key, code));
    }
}
