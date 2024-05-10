package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.oauth.SocialLoginType;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.LoginRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SignUpRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MembershipResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.service.RegisterMemberService;
import io.swagger.v3.oas.annotations.Operation;
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
                signUpRequest.getKey(),
                signUpRequest.getName(),
                signUpRequest.getGender(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getFcmToken()));
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
                type,
                loginRequest.getAccessToken(),
                loginRequest.getFcmToken()));
    }

    /**
     * [POST] 가입 여부 확인
     * /{type}/membership
     */
    @Operation(summary = "가입 여부 확인")
    @PostMapping("/{type}/membership")
    public ResponseEntity<MembershipResponse> checkMembership(
            @PathVariable(name = "type") SocialLoginType type,
            @Valid @RequestBody LoginRequest loginRequest) throws IOException {
        return ResponseEntity.ok(registerMemberService.checkMembership(
                type, loginRequest.getAccessToken()));
    }

    /**
     * [POST] 로그아웃
     * /logout
     */
    @Operation(summary = "로그아웃", description = "헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요!")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }
}
