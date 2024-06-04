package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.s3.S3Service;
import com.modutaxi.api.common.util.validator.NicknameValidator;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.mail.service.MailUtil;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CertificationResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenAndMemberResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.UpdateProfileResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.entity.Role;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.sms.service.SmsService;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateMemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final MailUtil mailUtil;
    private final SmsService smsService;
    private final S3Service s3Service;

    //TODO: Member Profile에 필요한 정보가 확정나면 다시 수정이 필요합니다.
    public TokenAndMemberResponse refreshAccessToken(Member member) {
        if (member.isBlocked()) {
            throw new BaseException(MemberErrorCode.BLOCKED_MEMBER);
        }
        return new TokenAndMemberResponse(
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
        return new CertificationResponse(
            mailService.sendEmailCertificationMail(memberId, receiver));
    }

    @Transactional
    public CertificationResponse checkEmailCertificationCode(Long memberId,
        String certificationCode) {
        String email = mailService.checkEmailCertificationCode(memberId, certificationCode);
        // 이메일 중복 체크
        getNotCertificatedMember(memberId, email);
        Member member = memberRepository.findByIdAndStatusTrue(memberId).get();
        member.certificateEmail(email);
        return new CertificationResponse(true);
    }

    private void getNotCertificatedMember(Long memberId, String email) {
        Optional<Member> member = memberRepository.findCertificatedMember(memberId, email,
            Role.ROLE_VISITOR);
        if (member.isEmpty()) {
            return;
        }
        if (member.get().getId() == memberId) {
            throw new BaseException(MailErrorCode.ALREADY_CERTIFIED_EMAIL);
        }
        throw new BaseException(MailErrorCode.USED_EMAIL);
    }

    public CertificationResponse sendSmsCertification(String signupKey, String phoneNumber) {
        return new CertificationResponse(smsService.sendCertificationCode(signupKey, phoneNumber));
    }

    public CertificationResponse checkSmsCertificationCode(String signupKey, String phoneNumber,
        String certificationCode) {
        return new CertificationResponse(
            smsService.checkSmsCertificationCode(signupKey, phoneNumber, certificationCode));
    }

    @Transactional
    public UpdateProfileResponse updateProfile(Member member, String nickname, String imageUrl) {
        checkNickname(nickname);
        // imageUrl == "" 로 들어오면 삭제 요청입니다.
        if (Objects.equals(imageUrl, "")) {
            if (member.existsNickname()) {   // 프로필 사진이 있었다면 s3에서 삭제
                s3Service.deleteFile(member.getImageUrl());
            }
            imageUrl = null;
        }
        member.updateProfile(nickname, imageUrl);
        memberRepository.save(member);
        return new UpdateProfileResponse(member.getNickname(), member.getImageUrl());
    }

    private void checkNickname(String nickname) {
        // 중복 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        // 그 외 필터링
        NicknameValidator.validate(nickname);
    }

}
