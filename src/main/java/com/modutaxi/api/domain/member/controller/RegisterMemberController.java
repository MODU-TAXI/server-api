package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.auth.oauth.SocialLoginType;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.LoginRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.NicknameRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SignUpRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MembershipResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.NicknameResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenAndMemberResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.RegisterMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "회원 가입", description = "회원 가입 API")
public class RegisterMemberController {

    private final RegisterMemberService registerMemberService;

    /**
     * [POST] 소셜 회원가입 /sign-up
     */
    @Operation(summary = "소셜 회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<TokenAndMemberResponse> register(
        @Valid @RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok(registerMemberService.registerMember(
            signUpRequest.getKey(),
            signUpRequest.getName(),
            signUpRequest.getGender(),
            signUpRequest.getPhoneNumber(),
            signUpRequest.getFcmToken()));
    }

    /**
     * [POST] 소셜 로그인 /{type}/login
     */
    @Operation(
        summary = "소셜 로그인",
        description = "type: KAKAO, APPLE<br>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "409", description = "로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_001", description = "존재하지 않는 사용자", value = """
                {
                    "errorCode": "MEMBER_001",
                    "message": "존재하지 않는 사용자입니다."
                }
                """),
        })),
        @ApiResponse(responseCode = "409", description = "로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_010", description = "신고 누적으로 인해 임시 차단", value = """
                {
                    "errorCode": "MEMBER_010",
                    "message": "임시 차단된 사용자입니다."
                }
                """),
        })),
    })
    @PostMapping("/{type}/login")
    public ResponseEntity<TokenAndMemberResponse> login(
        @PathVariable(name = "type") SocialLoginType type,
        @Valid @RequestBody LoginRequest loginRequest) throws IOException {
        return ResponseEntity.ok(registerMemberService.login(
            type,
            loginRequest.getAccessToken(),
            loginRequest.getFcmToken()));
    }

    /**
     * [POST] 가입 여부 확인 /{type}/membership
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
     * [POST] 로그아웃 /logout
     */
    @Operation(summary = "로그아웃", description = "헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요!")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.ok().build();
    }

    /**
     * [POST] 닉네임 설정 /api/members/nickname
     */
    @Operation(
        summary = "닉네임 설정",
        description = "닉네임을 설정합니다.<br>" +
            "헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요!"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "닉네임 설정 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = NicknameResponse.class))),
        @ApiResponse(responseCode = "400", description = "닉네임 설정 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_003", description = "닉네임 중복", value = """
                {
                    "errorCode": "MEMBER_003",
                    "message": "이미 있는 닉네임이에요!"
                }
                """),
            @ExampleObject(name = "MEMBER_006", description = "공백, 특수문자 포함", value = """
                {
                    "errorCode": "MEMBER_006",
                    "message": "공백, 특수문자는 사용할 수 없어요!"
                }
                """),
            @ExampleObject(name = "MEMBER_007", description = "닉네임 최소 길이 제한", value = """
                {
                    "errorCode": "MEMBER_007",
                    "message": "닉네임은 최소 2글자부터 가능해요!"
                }
                """),
            @ExampleObject(name = "MEMBER_008", description = "닉네임 최대 길이 제한", value = """
                {
                    "errorCode": "MEMBER_008",
                    "message": "닉네임은 최대 12글자까지 가능해요!"
                }
                """),
            @ExampleObject(name = "MEMBER_009", description = "비속어 혹은 부적절한 단어", value = """
                {
                    "errorCode": "MEMBER_009",
                    "message": "비속어 혹은 부적절한 단어가 포함된 닉네임은 생성할 수 없어요!"
                }
                """),
        })),
    })
    @PostMapping("/nickname")
    public ResponseEntity<NicknameResponse> registerNickname(
        @CurrentMember Member member,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = NicknameRequest.class)))
        @RequestBody NicknameRequest request
    ) {
        return ResponseEntity.ok(registerMemberService.registerNickname(
            member, request.getNickname()));
    }
}
