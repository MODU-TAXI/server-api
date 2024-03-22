package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.auth.oauth.SocialLoginService;
import com.modutaxi.api.common.auth.oauth.SocialLoginType;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.CheckNameResponse;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Gender;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.member.repository.RedisSnsIdRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class RegisterMemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialLoginService socialLoginService;
    private final RedisSnsIdRepositoryImpl redisSnsIdRepository;

    /**
     * 회원 가입
     */
    public TokenResponse registerMember(String key, String name, Gender gender) {
        // key를 이용하여 redis 에서 snsId 추출, 삭제
        String snsId = redisSnsIdRepository.findById(key);
        // DB에 가입 이력 있는지 중복 확인
        checkRegister(snsId, name);
        // member entity 생성
        Member member = memberMapper.ToEntity(snsId, name, gender);
        memberRepository.save(member);
        // 로그인 토큰 생성 및 저장
        return generateMemberToken(member);
    }

    private void checkRegister(String snsId, String name) {
        // 계정 중복 체크
        if (memberRepository.existsBySnsId(snsId)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        } // 닉네임 중복 체크
        if (memberRepository.existsByName(name)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

    /**
     * 닉네임 중복 체크
     */
    public CheckNameResponse checkName(String name) {
        return new CheckNameResponse(!memberRepository.existsByName(name));
    }

    /**
     * 로그인
     */
    public TokenResponse login(SocialLoginType type, String accessToken) throws IOException {
        String snsId = "";
        switch (type) {
            case KAKAO -> snsId = socialLoginService.getKaKaoSnsId(accessToken);
            // TODO: 애플 로그인 구현
            case APPLE -> snsId = "";
        }
        // 존재하지 않는다면 UN_REGISTERED_MEMBER 에러에 redis snsId key를 담아서 내려줌
        String key = redisSnsIdRepository.save(snsId);
        Member member = memberRepository.findBySnsId(snsId)
                .orElseThrow(() -> new BaseException(
                        MemberErrorCode.UN_REGISTERED_MEMBER,
                        key));
        // 존재하는 멤버라면 토큰 발급
        return generateMemberToken(member);
    }

    /**
     * 로그인 토큰 생성 및 리프레시 토큰 저장 함수
     */
    private TokenResponse generateMemberToken(Member member) {
        TokenResponse tokenResponse = jwtTokenProvider.generateToken(member.getId());
        member.changeRefreshToken(tokenResponse.getRefreshToken());
        memberRepository.save(member);
        return tokenResponse;
    }
}
