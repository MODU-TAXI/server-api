package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.domain.member.dto.MemberRequestDto.SignUpRequest;
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
     * TODO: 온보딩 작업되면 request에 문항 답변도 추가
     */
    @PostMapping("/sign-up")
    public ResponseEntity<TokenResponse> register(
            @Valid @RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(registerMemberService.registerMember(
                signUpRequest.getEmail(), signUpRequest.getName()),
                HttpStatus.OK);
    }
}
