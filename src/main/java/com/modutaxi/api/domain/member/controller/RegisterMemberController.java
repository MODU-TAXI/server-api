package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.domain.member.dto.MemberRequestDto.CheckNameRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.LoginRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SignUpRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CheckNameResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.service.RegisterMemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RegisterMemberController {

    private final RegisterMemberService registerMemberService;

    /**
     * [POST] 소셜 회원가입
     * /sign-up
     */
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(registerMemberService.registerMember(
                signUpRequest.getEmail(), signUpRequest.getName()),
                HttpStatus.OK);
    }

    /**
     * [POST] 닉네임 중복 확인
     * /names
     */
    @PostMapping("/names")
    public ResponseEntity<CheckNameResponse> checkName(
            @Valid @RequestBody CheckNameRequest checkNameRequest) {
        return new ResponseEntity<>(registerMemberService.checkName(
                checkNameRequest.getName()),
                HttpStatus.OK);
    }

    /**
     * [POST] 로그인
     * /login
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(registerMemberService.login(
                loginRequest.getEmail()),
                HttpStatus.OK);
    }


}
