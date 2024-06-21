package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.ConfirmMailCertificationReqeust;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.ConfirmSmsCertificationReqeust;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SendMailCertificationRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.SendSmsCertificationRequest;
import com.modutaxi.api.domain.member.dto.MemberRequestDto.UpdateProfileRequest;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CertificationResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenAndMemberResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.UpdateProfileResponse;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<TokenAndMemberResponse> refreshLogin(
        @CurrentMember Member member) {
        return new ResponseEntity<>(updateMemberService.refreshAccessToken(member),
            HttpStatus.OK);
    }

    /**
     * [GET] 이메일 인증 메일 발송 /api/members/mail/certificate
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
        return ResponseEntity.ok(updateMemberService.sendEmailCertificationMail(member.getId(),
            request.getMailAddress()));
    }

    /**
     * [GET] 이메일 인증 확인 /api/members/mail/confirm
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
        return ResponseEntity.ok(
            updateMemberService.checkEmailCertificationCode(member.getId(), request.getCertCode()));
    }

    @Operation(
        summary = "SMS 인증 메시지 발송",
        description = "SMS 인증 메시지를 발송합니다.<br>**문자 발송에 건당 8.4원 씩 비용이 발생하므로 주의해주세요.**<br>로그인 시도 실패시 발급된 key, 인증번호 발급에 사용할 휴대폰 번호를 입력해주세요.<br>휴대전화 번호의 형식은 010-1234-5678 과 같이 '-'와 함께 요청해야합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문자 발송 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CertificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "문자 발송 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SmsErrorCode.class), examples = {
            @ExampleObject(name = "SMS_004", description = "같은 조건의 짧은 기간의 요청", value = """
                {
                    "errorCode": "SMS_004",
                    "message": "이미 인증번호가 발송되었습니다. 잠시후 재시도 해주세요."
                }
                """),
            @ExampleObject(name = "SMS_005", description = "짧은 기간의 요청이거나 통신업체의 발송지연", value = """
                {
                    "errorCode": "SMS_005",
                    "message": "인증번호가 발송중입니다."
                }
                """),
            @ExampleObject(name = "SMS_006", description = "휴대전화 번호 패턴 불일치", value = """
                {
                    "errorCode": "SMS_006",
                    "message": "유효하지 않은 전화번호 형식입니다."
                }
                """),

            @ExampleObject(name = "SMS_008", description = "SMS 서비스 사업자와의 통신 문제", value = """
                {
                    "errorCode": "SMS_008",
                    "message": "SMS 발송에 실패했습니다. 잠시후 재시도 해주세요."
                }
                """),
        }))
    })
    @PostMapping("/sms/certificate")
    public ResponseEntity<CertificationResponse> sendSmsCertification(
        @RequestBody SendSmsCertificationRequest request) {
        return ResponseEntity.ok(
            updateMemberService.sendSmsCertification(request.getKey(), request.getPhoneNumber()));
    }

    @Operation(
        summary = "SMS 인증 확인",
        description = "수신한 SMS 인증 코드를 인증합니다.<br>로그인 시도 실패시 발급된 key, 인증번호 발급에 사용한 휴대폰 번호, 인증번호를 입력해주세요.<br>휴대전화 번호의 형식은 010-1234-5678 과 같이 '-'와 함께 요청해야하며, 인증번호는 6자리 숫자입니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "문자 인증 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CertificationResponse.class))),
        @ApiResponse(responseCode = "400", description = "문자 인증 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SmsErrorCode.class), examples = {
            @ExampleObject(name = "SMS_001", description = "로그인 시도 실패시 발급된 key로의 인증번호 미발급 또는 인증번호 만료", value = """
                {
                    "errorCode": "SMS_001",
                    "message": "인증번호가 만료되었습니다."
                }
                """),
            @ExampleObject(name = "SMS_002", description = "인증 수신 휴대전화 번호와 인증 번호화 함께 보낸 휴대전화 번호의 불일치", value = """
                {
                    "errorCode": "SMS_002",
                    "message": "인증번호를 요청한 번호와 일치하지 않습니다."
                }
                """),
            @ExampleObject(name = "SMS_003", description = "인증번호 불일치", value = """
                {
                    "errorCode": "SMS_003",
                    "message": "인증번호가 일치하지 않습니다."
                }
                """),
            @ExampleObject(name = "SMS_006", description = "휴대전화 번호 패턴 불일치", value = """
                {
                    "errorCode": "SMS_006",
                    "message": "유효하지 않은 전화번호 형식입니다."
                }
                """),
            @ExampleObject(name = "SMS_007", description = "인증번호 패턴 불일치", value = """
                {
                    "errorCode": "SMS_007",
                    "message": "유효하지 않은 인증코드 형식입니다."
                }
                """),
        }))
    })
    @PostMapping("/sms/confirm")
    public ResponseEntity<CertificationResponse> confirmSmsCertification(
        @RequestBody ConfirmSmsCertificationReqeust request) {
        return ResponseEntity.ok(updateMemberService.checkSmsCertificationCode(request.getKey(),
            request.getPhoneNumber(), request.getCertificationCode()));
    }

    /**
     * [PATCH] 멤버 프로필 변경 /api/members
     */
    @Operation(
        summary = "멤버 프로필 변경",
        description = "멤버 프로필을 변경합니다.<br>" +
            "헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요!<br>" +
            "GENDER: MALE, FEMALE 중 1 입니다.<br>" +
            "등록한 프로필 사진을 삭제하고 싶다면 blank(\"\")를 보내주세요!. 프로필 사진을 변경하지 않고 싶으면, 현재 프로필 사진 URL을 보내주세요."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 변경 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateProfileResponse.class))),
    })
    @PatchMapping("")
    public ResponseEntity<UpdateProfileResponse> updateMemberProfile(
        @CurrentMember Member member,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = UpdateProfileRequest.class)))
        @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(updateMemberService.updateProfile(
            member, request.getName(), request.getGender(), request.getPhoneNumber(),
            request.getImageUrl()));
    }

    /**
     * [DELETE] 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴",
        description = "회원 탈퇴입니다.<br>헤더에 반드시 Authorization 으로 accessToken 값을 넣어주세요!<br>"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공", content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("")
    public ResponseEntity<Integer> deleteMember(
        @CurrentMember Member member) {
        updateMemberService.deleteMember(member);
        return ResponseEntity.ok(200);
    }
}
