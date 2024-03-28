package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.mail.service.MailUtil;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.entity.Role;
import com.modutaxi.api.domain.member.repository.MemberRepository;
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

    public TokenResponse refreshAccessToken(Member member) {
        TokenResponse newTokenResponse = jwtTokenProvider.generateToken(member.getId());
        return new TokenResponse(newTokenResponse.getAccessToken(), member.getRefreshToken());
    }

    public Boolean sendEmailCertificationMail(Long memberId, String receiver) {
        // 이메일 형식 체크
        if (!mailUtil.emailAddressFormVerification(receiver)) {
            throw new BaseException(MailErrorCode.INVALID_EMAIL_FORM);
        }
        // 지원 이메일 도메인 체크
        if (!mailService.checkMailDomain(receiver)) {
            throw new BaseException(MailErrorCode.UNSUPPOERTED_DOMAIN);
        }
        // 이메일 중복 체크
        getNotCertificatedMember(memberId, receiver);
        // 이메일 발송
        return mailService.sendEmailCertificationMail(memberId, receiver);
    }

    @Transactional
    public Boolean checkEmailCertificationCode(Long memberId, String certificationCode) {
        String email = mailService.checkEmailCertificationCode(memberId, certificationCode);
        // 이메일 중복 체크
        getNotCertificatedMember(memberId, email);
        Member member = memberRepository.findByIdAndStatusTrue(memberId).get();
        member.certificateEmail(email);
        return true;
    }

    private void getNotCertificatedMember(Long memberId, String email) {
        Optional<Member> member = memberRepository.findCertificatedMember(memberId, email, Role.ROLE_VISITOR);
        if (member.isEmpty()) return;
        if (member.get().getId() == memberId) throw new BaseException(MailErrorCode.ALREADY_CERTIFIED_EMAIL);
        throw new BaseException(MailErrorCode.USED_EMAIL);
    }
}
