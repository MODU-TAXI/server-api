package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.UpdateMemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UpdateMemberController {

    private final UpdateMemberService updateMemberService;

    /**
     * [PATCH] 로그인 토큰 갱신
     */
    @Operation(summary = "로그인 토큰 갱신")
    @PatchMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshLogin(
            @CurrentMember Member member) {
        return new ResponseEntity<>(updateMemberService.refreshAccessToken(member),
                HttpStatus.OK);
    }
}
