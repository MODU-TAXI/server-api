package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.common.s3.S3Service;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.alarm.repository.AlarmRepository;
import com.modutaxi.api.domain.history.repository.HistoryRepository;
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
import com.modutaxi.api.domain.participant.repository.ParticipantRepository;
import com.modutaxi.api.domain.participant.service.UpdateParticipantService;
import com.modutaxi.api.domain.paymentmember.service.UpdatePaymentMemberService;
import com.modutaxi.api.domain.room.repository.RoomRepository;
import com.modutaxi.api.domain.room.service.UpdateRoomService;
import com.modutaxi.api.domain.roomwaiting.repository.RoomWaitingRepository;
import com.modutaxi.api.domain.sms.service.SmsService;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Log4j2
@Transactional
@RequiredArgsConstructor
public class UpdateMemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final MailUtil mailUtil;
    private final SmsService smsService;
    private final S3Service s3Service;
    private final UpdateRoomService updateRoomService;
    private final UpdatePaymentMemberService updatePaymentMemberService;
    private final UpdateParticipantService updateParticipantService;

    private final AccountRepository accountRepository;
    private final AlarmRepository alarmRepository;
    private final HistoryRepository historyRepository;
    private final ParticipantRepository participantRepository;
    private final RoomRepository roomRepository;
    private final RoomWaitingRepository roomWaitingRepository;

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

    public CertificationResponse sendSmsCertificationWithSignupKey(String signupKey,
        String phoneNumber) {
        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BaseException(SmsErrorCode.ALREADY_USED_PHONE_NUMBER);
        }
        return new CertificationResponse(
            smsService.sendCertificationCodeWithSignupKey(signupKey, phoneNumber));
    }

    public CertificationResponse sendSmsCertificationWithJwt(Long memberId, String phoneNumber) {
        if (memberRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new BaseException(SmsErrorCode.ALREADY_USED_PHONE_NUMBER);
        }
        return new CertificationResponse(
            smsService.sendCertificationCodeWithJwt(memberId.toString(), phoneNumber));
    }

    public CertificationResponse checkSmsCertificationCodeWithSignupKey(String signupKey,
        String phoneNumber,
        String certificationCode) {
        return new CertificationResponse(
            smsService.checkSmsCertificationCodeWithSignupKey(signupKey, phoneNumber,
                certificationCode));
    }

    public CertificationResponse checkSmsCertificationCodeWithJwt(Long memberId, String phoneNumber,
        String certificationCode) {
        return new CertificationResponse(
            smsService.checkSmsCertificationCodeWithJwt(memberId.toString(), phoneNumber,
                certificationCode));
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
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(
            () -> new BaseException(MemberErrorCode.EMPTY_MEMBER)
        );
        // 멤버 soft delete
        member.delete();

        deleteRoomMapping(member);
        deleteMemberInfo(member);
    }

    /**
     * 멤버의 방 맵핑 삭제
     */
    public void deleteRoomMapping(Member member) {
        // 모든 대기열 삭제
        roomWaitingRepository.deleteByMember(member);
        log.info("대기열 탈퇴 성공!");
        // 내가 방장인 방이 있다면, 방 삭제
        if (roomRepository.existsRoomByRoomManager(member)) {
            log.info("내가 방장인 방이 있나요? 결과: {}", roomRepository.existsRoomByRoomManager(member));
            Long roomId = roomRepository.findIdByRoomManagerAndRoomStatusIsNotDelete(
                member);   // 내가 이용 중인 방 ID
            log.info("내 방의 ID는? 결과: {}", roomId);
            updateRoomService.deleteRoom(member, roomId);
            roomRepository.updateMemberId(roomId, 0L);
            log.info("방 삭제 성공!");
        }
        // 내가 방장이 아니고 이용 중인 방이 있다면, 방 퇴장
        else if (participantRepository.existsByMember(member)) {
            log.info("내가 이용 중인 방이 있나요? 결과: {}", participantRepository.existsByMember(member));
            updateParticipantService.leaveRoomAndDeleteChatRoomInfo(member.getId());
            log.info("방 퇴장 성공!");
        }
    }

    /**
     * 멤버의 정보와 관련된 것들 삭제
     */
    public void deleteMemberInfo(Member member) {
        // PaymentMember hard delete
        updatePaymentMemberService.deleteByMember(member);
        log.info("정산 정보 삭제 성공!");
        // 이용 내역 hard delete
        historyRepository.deleteByMember(member);
        log.info("이용 내역 삭제 성공!");
        // 계좌 정보 hard delete
        accountRepository.deleteByMember(member);
        log.info("계좌 삭제 성공!");
        // 알림 hard delete
        alarmRepository.deleteByMemberId(member.getId());
        // 이용 내역 hard delete
        historyRepository.deleteByMember(member);
        log.info("알림 삭제 성공!");
    }

}
