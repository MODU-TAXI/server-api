package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.common.s3.S3Service;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.alarm.repository.AlarmRepository;
import com.modutaxi.api.domain.likedSpot.repository.LikedSpotRepository;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.mail.service.MailUtil;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CertificationResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenAndMemberResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.UpdateProfileResponse;
import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.entity.Role;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.sms.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

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

    private final AccountRepository accountRepository;
    private final AlarmRepository alarmRepository;
    private final LikedSpotRepository likedSpotRepository;

    //TODO: Member Profile에 필요한 정보가 확정나면 다시 수정이 필요합니다.
    public TokenAndMemberResponse refreshAccessToken(Member member) {
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

    public CertificationResponse sendSmsCertificationWithSignupKey(String signupKey, String phoneNumber) {
        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BaseException(SmsErrorCode.ALREADY_USED_PHONE_NUMBER);
        }
        return new CertificationResponse(smsService.sendCertificationCodeWithSignupKey(signupKey, phoneNumber));
    }

    public CertificationResponse sendSmsCertificationWithJwt(Long memberId, String phoneNumber) {
        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BaseException(SmsErrorCode.ALREADY_USED_PHONE_NUMBER);
        }
        return new CertificationResponse(smsService.sendCertificationCodeWithJwt(memberId.toString(), phoneNumber));
    }

    public CertificationResponse checkSmsCertificationCodeWithSignupKey(String signupKey, String phoneNumber,
                                                                        String certificationCode) {
        return new CertificationResponse(
            smsService.checkSmsCertificationCodeWithSignupKey(signupKey, phoneNumber, certificationCode));
    }

    public CertificationResponse checkSmsCertificationCodeWithJwt(Long memberId, String phoneNumber,
                                                           String certificationCode) {
        return new CertificationResponse(
            smsService.checkSmsCertificationCodeWithJwt(memberId.toString(), phoneNumber, certificationCode));
    }

    @Transactional
    public UpdateProfileResponse updateProfile(Member member, String name, Gender gender,
                                               String phoneNumber, String imageUrl) {
        // imageUrl == "" 로 들어오면 삭제 요청입니다.
        if (Objects.equals(imageUrl, "")) {
            if (member.existsImageUrl()) {   // 프로필 사진이 있었다면 s3에서 삭제
                s3Service.deleteFile(member.getImageUrl());
            }
            imageUrl = null;
        }
        member.updateProfile(name, gender, phoneNumber, imageUrl);
        memberRepository.save(member);
        return new UpdateProfileResponse(member.getName(), member.getGender(),
            member.getPhoneNumber(), member.getImageUrl());
    }

    @Transactional
    public void deleteMember(Member member) {
        // 멤버 soft delete
        member.delete();
        // 계좌 정보 hard delete
        accountRepository.deleteByMember(member);
        // 알림 hard delete
        alarmRepository.deleteByMemberId(member.getId());
        // 즐겨찾기 hard delete
        likedSpotRepository.deleteByMember(member);
    }

}
