package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.dto.MemberResponseDto;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MailResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.UpdateMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "회원 정보 수정", description = "회원 정보 수정 API")
public class UpdateMemberController {

    private final UpdateMemberService updateMemberService;

    /**
     * [PATCH] 로그인 토큰 갱신
     */
    @Operation(
            summary = "로그인 토큰 갱신",
            description = "Header에 refreshToken을 넣어보내주세요.<br>" +
                    "key: refreshToken, value: ${refreshToken}."
    )
    @PatchMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshLogin(
            @CurrentMember Member member) {
        return new ResponseEntity<>(updateMemberService.refreshAccessToken(member.getId()),
                HttpStatus.OK);
    }

    /**
     * [GET] 이메일 인증 메일 발송
     * /mail-cert
     */
    @Operation(
        summary = "이메일 인증 메일 발송",
        description = "이메일 인증 메일을 발송합니다.<br>" +
            "인증메일을 요청한 메일 주소로 인증 메일을 발송합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메일 발송 성공"),
        @ApiResponse(responseCode = "400", description = "메일 발송 실패"),
        @ApiResponse(responseCode = "429", description = "단기간 중복 메일 발송 요청"),
    })
    @GetMapping("/mail/certificate")
    public ResponseEntity<MailResponse> sendEmailCertificationMail(
        @CurrentMember Member member,
        @Parameter(description = "인증 메일을 받을 이메일 주소")
        @RequestParam String receiver
    ) {
        return ResponseEntity.ok(updateMemberService.sendEmailCertificationMail(member.getId(), receiver));
    }

    /**
     * [GET] 이메일 인증 확인
     * /mail/confirm
     */
    @Operation(
        summary = "이메일 인증 확인",
        description = "수신한 인증 메일을 인증합니다.<br>" +
            "인증코드를 인증합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "메일 인증 성공"),
        @ApiResponse(responseCode = "400", description = "메일 인증 실패")
    })
    @GetMapping("/mail/confirm")
    public ResponseEntity<MailResponse> confirmEmailCertification(
        @CurrentMember Member member,
        @Parameter(description = "인증 메일로 받은 인증 코드")
        @RequestParam String code) {
        return ResponseEntity.ok(updateMemberService.checkEmailCertificationCode(member.getId(), code));
    }
}
