package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.mapper.MemberMapper;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterMemberService {

    private final MemberMapper memberMapper;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원 가입
     */
    public TokenResponse registerMember(String email, String name) {
        // DB에 가입 이력 있는지 중복 확인
        checkRegister(email, name);
        // member entity 생성
        Member member = memberMapper.ToEntity(email, name);
        // 로그인 토큰 생성 및 저장
        TokenResponse tokenResponse = jwtTokenProvider.generateToken(member.getId());
        member.changeRefreshToken(tokenResponse.getRefreshToken());
        return tokenResponse;
    }

    private void checkRegister(String email, String name) {
        if (memberRepository.existsByEmail(email)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_MEMBER);
        }
        if (memberRepository.existsByName(name)) {
            throw new BaseException(MemberErrorCode.DUPLICATE_NICKNAME);
        }
    }

}
