package com.modutaxi.api.domain.member.service;

import com.modutaxi.api.common.auth.jwt.JwtTokenProvider;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateMemberService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    public TokenResponse refreshAccessToken(Long memberId) {
        return jwtTokenProvider.generateToken(memberId);
    }

}
