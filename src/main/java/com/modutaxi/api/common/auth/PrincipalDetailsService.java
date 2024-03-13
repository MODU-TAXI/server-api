package com.modutaxi.api.common.auth;

import com.modutaxi.api.domain.member.Member;
import com.modutaxi.api.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    /**
     * token 안의 사용자 정보를 받아 실제 DB 에서 사용자 인증 정보를 가져오는 함수
     * @param username 이름은 username 이지만, memberId를 나타냄
     * @return PrincipalDetails 객체 반환
     * @throws UsernameNotFoundException 해당 멤버 객체를 찾지 못했을 때
     */
    @Override
    public PrincipalDetails loadUserByUsername(String username)
        throws UsernameNotFoundException {
        Member member = memberRepository.findByIdAndStatusTrue(Long.parseLong(username))
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        return new PrincipalDetails(member);
    }
}
