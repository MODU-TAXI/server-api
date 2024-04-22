package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.ConfirmMailCertificationReqeust;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SendMailCertificationRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CertificationResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.UpdateMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
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
     * /api/members/mail/certificate
     */
    @Operation(
            summary = "이메일 인증 메일 발송",
            description = "이메일 인증 메일을 발송합니다.<br>" +
                    "인증메일을 요청한 메일 주소로 인증 메일을 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 발송 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CertificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "메일 발송 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MailErrorCode.class), examples = {
                    @ExampleObject(name = "MAIL_001", description = "메일 발송 단계에서 실패했습니다.", value = """
                            {
                                "errorCode": "MAIL_001",
                                "message": "메일 발송에 실패했습니다. 서버 관리자에게 문의해주세요."
                            }
                            """),
                    @ExampleObject(name = "MAIL_003", description = "이메일 형식 문제", value = """
                            {
                                "errorCode": "MAIL_003",
                                "message": "유효하지 않은 이메일 형식입니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_004", description = "지원하지 않는 도메인", value = """
                            {
                                "errorCode": "MAIL_004",
                                "message": "지원하지 않는 도메인입니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_005", description = "사용중인 이메일 주소", value = """
                            {
                                "errorCode": "MAIL_005",
                                "message": "이미 사용중인 이메일입니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_009", description = "인증된 메일 주소가 존재하는 계정", value = """
                            {
                                "errorCode": "MAIL_009",
                                "message": "이미 인증된 계정입니다."
                            }
                            """),
            })),
            @ApiResponse(responseCode = "429", description = "단기간 중복 메일 발송 요청", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MailErrorCode.class), examples = {
                    @ExampleObject(name = "MAIL_008", description = "단기간에 여러번의 인증메일을 요청할 수 없습니다.", value = """
                            {
                                "errorCode": "MAIL_008",
                                "message": "이미 발송된 인증메일 요청입니다."
                            }
                            """),
            })),
    })
    @PostMapping("/mail/certificate")
    public ResponseEntity<CertificationResponse> sendEmailCertificationMail(
            @CurrentMember Member member,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = SendMailCertificationRequest.class)))
            @RequestBody SendMailCertificationRequest request
    ) {
        return ResponseEntity.ok(updateMemberService.sendEmailCertificationMail(member.getId(), request.getMailAddress()));
    }

    /**
     * [GET] 이메일 인증 확인
     * /api/members/mail/confirm
     */
    @Operation(
            summary = "이메일 인증 확인",
            description = "수신한 인증 메일을 인증합니다.<br>" +
                    "인증코드를 인증합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 인증 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CertificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "메일 인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MailErrorCode.class), examples = {
                    @ExampleObject(name = "MAIL_001", description = "메일 발송 단계에서 실패했습니다.", value = """
                            {
                                "errorCode": "MAIL_001",
                                "message": "인증 코드가 만료되었거나 존재하지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_005", description = "사용중인 이메일 주소", value = """
                            {
                                "errorCode": "MAIL_005",
                                "message": "이미 사용중인 이메일입니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_007", description = "발송된 인증 코드와 일치 하지 않음", value = """
                            {
                                "errorCode": "MAIL_007",
                                "message": "인증 코드가 일치하지 않습니다."
                            }
                            """),
                    @ExampleObject(name = "MAIL_009", description = "인증된 메일 주소가 존재하는 계정", value = """
                            {
                                "errorCode": "MAIL_009",
                                "message": "이미 인증된 계정입니다."
                            }
                            """),
            })),
    })
    @PostMapping("/mail/confirm")
    public ResponseEntity<CertificationResponse> confirmEmailCertification(
            @CurrentMember Member member,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = ConfirmMailCertificationReqeust.class)))
            @RequestBody ConfirmMailCertificationReqeust request) {
        return ResponseEntity.ok(updateMemberService.checkEmailCertificationCode(member.getId(), request.getCertCode()));
    }
}
