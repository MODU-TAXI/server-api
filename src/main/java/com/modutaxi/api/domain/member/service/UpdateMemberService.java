package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.mail.service.MailUtil;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CertificationResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.RefreshTokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.entity.Role;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateMemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final MailUtil mailUtil;
    private final SmsService smsService;

    public RefreshTokenResponse refreshAccessToken(Member member) {
        return new RefreshTokenResponse(
                jwtTokenProvider.generateToken(member.getId()),
                MemberMapper.toDto(member)
        );
    }

    public CertificationResponse sendEmailCertificationMail(Long memberId, String receiver) {
        // 이메일 형식 체크
        if (!mailUtil.emailAddressFormVerification(receiver)) {
            throw new BaseException(MailErrorCode.INVALID_EMAIL_FORM);
        }
        // 지원 이메일 도메인 체크
        if (!mailService.checkMailDomain(receiver)) {
            throw new BaseException(MailErrorCode.UNSUPPORTED_DOMAIN);
        }
        // 이메일 중복 체크
        getNotCertificatedMember(memberId, receiver);
        // 이메일 발송
        return new CertificationResponse(mailService.sendEmailCertificationMail(memberId, receiver));
    }

    @Transactional
    public CertificationResponse checkEmailCertificationCode(Long memberId, String certificationCode) {
        String email = mailService.checkEmailCertificationCode(memberId, certificationCode);
        // 이메일 중복 체크
        getNotCertificatedMember(memberId, email);
        Member member = memberRepository.findByIdAndStatusTrue(memberId).get();
        member.certificateEmail(email);
        return new CertificationResponse(true);
    }

    private void getNotCertificatedMember(Long memberId, String email) {
        Optional<Member> member = memberRepository.findCertificatedMember(memberId, email, Role.ROLE_VISITOR);
        if (member.isEmpty()) return;
        if (member.get().getId() == memberId) throw new BaseException(MailErrorCode.ALREADY_CERTIFIED_EMAIL);
        throw new BaseException(MailErrorCode.USED_EMAIL);
    }

    public CertificationResponse sendSmsCertification(String signupKey, String phoneNumber) {
        return new CertificationResponse(smsService.sendCertificationCode(signupKey, phoneNumber));
    }

    public CertificationResponse checkSmsCertificationCode(String signupKey, String phoneNumber, String certificationCode) {
        return new CertificationResponse(smsService.checkSmsCertificationCode(signupKey, phoneNumber, certificationCode));
    }
}
