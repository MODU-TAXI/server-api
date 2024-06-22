package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.auth.oauth.SocialLoginService;
import com.modutaxi.api.common.auth.oauth.SocialLoginType;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.fcm.RedisFcmRepositoryImpl;
import com.modutaxi.api.common.util.validator.NicknameValidator;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MembershipResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.NicknameResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenAndMemberResponse;
import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.member.repository.RedisSnsIdRepositoryImpl;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@RequiredArgsConstructor
public class RegisterMemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialLoginService socialLoginService;
    private final RedisSnsIdRepositoryImpl redisSnsIdRepository;
    private final RedisFcmRepositoryImpl redisFcmRepository;

    /**
     * 회원 가입
     */
    public TokenAndMemberResponse registerMember(String key, String name, Gender gender,
        String phoneNumber,
        String fcmToken) {
        // key를 이용하여 redis 에서 snsId 추출, 삭제
        String snsId = checkSnsIdKey(key);
        // DB에 가입 이력 있는지 중복 확인
        checkRegister(snsId);
        // member entity 생성
        Member member = MemberMapper.toEntity(snsId, name, gender, phoneNumber);
        memberRepository.save(member);
        // FCM 토큰 저장
        saveFcmToken(member.getId(), fcmToken);
        // 로그인 토큰 생성 및 저장
        return generateMemberToken(member);
    }

    private void checkRegister(String snsId) {
        // 계정 중복 체크
        if (memberRepository.existsBySnsId(snsId)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        }
    }

    private String checkSnsIdKey(String key) {
        String snsId = redisSnsIdRepository.findAndDeleteById(key);
        if (snsId == null) {
            throw new BaseException(AuthErrorCode.INVALID_SNS_ID_KEY);
        }
        return snsId;
    }

    /**
     * 로그인
     * 가입된 멤버 -> TokenAndMemberResponse
     * 가입되지 않은 멤버 -> MembershipResponse
     */
    public <T> T login(SocialLoginType type, String accessToken, String fcmToken)
        throws IOException {
        String snsId = getSnsIdByAccessToken(type, accessToken);
        Member member = memberRepository.findBySnsIdAndStatusTrue(snsId).orElse(null);
        // 가입된 멤버라면
        if (member != null) {
            // FCM 토큰 저장
            saveFcmToken(member.getId(), fcmToken);
            return (T) generateMemberToken(member);
        } else {
            String key = redisSnsIdRepository.save(snsId);
            return (T) new MembershipResponse(key);
        }
    }

    /**
     * 닉네임 유효성 검사 및 등록 성공 시 닉네임 그대로 반환
     */
    public NicknameResponse registerNickname(Member member, String nickname) {
        checkNickname(nickname);
        member.changeNickname(nickname);
        memberRepository.save(member);
        return new NicknameResponse(member.getNickname());
    }

    private void checkNickname(String nickname) {
        // 중복 체크
        if (memberRepository.existsByNickname(nickname)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
        // 그 외 필터링
        NicknameValidator.validate(nickname);
    }

    /**
     * SocialLoginType과 accessToken을 받아 snsId 반환
     */
    private String getSnsIdByAccessToken(SocialLoginType type, String accessToken)
        throws IOException {
        return switch (type) {
            case KAKAO -> socialLoginService.getKaKaoSnsId(accessToken);
            case APPLE -> socialLoginService.getAppleSub(accessToken);
        };
    }

    /**
     * 로그인 토큰 생성 및 리프레시 토큰 저장 함수
     */
    private TokenAndMemberResponse generateMemberToken(Member member) {
        return new TokenAndMemberResponse(
            jwtTokenProvider.generateToken(member.getId()),
            MemberMapper.toDto(member)
        );
    }

    /**
     * FCM 토큰 저장 함수
     */
    private void saveFcmToken(Long memberId, String fcmToken) {
        redisFcmRepository.save(memberId, fcmToken);
    }

}
