package com.modutaxi.api.common.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 사용자가 로그인 정보와 함께 인증 요청이 들어오면 인증을 요구
     * 사용자 정보를 비교하여 인증이 완료되면 Authentication 객체를 반환 받아와서
     * SecurityContextHodler 가 Security Context 에 저장
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        Authentication authentication = null;
        String refreshToken = removeBearer(
                jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request));
        String token = removeBearer(
                jwtTokenProvider.resolveAccessToken((HttpServletRequest) request));
        // 헤더에 refreshToken을 보냈을 경우
        if (refreshToken != null && ((HttpServletRequest) request).getRequestURI().equals("/api/members/refresh")) {
            jwtTokenProvider.validateRefreshToken(refreshToken); // 유효성 검사
            authentication = jwtTokenProvider.getRefreshAuthentication(refreshToken); // authentication 세팅
        }
        // 헤더에 aceessToken을 보냈을 경우
        else if (token != null) {
            jwtTokenProvider.validateAccessToken(token); // 유효성 검사
            authentication = jwtTokenProvider.getAccessAuthentication(token); // authentication 세팅
            // 로그아웃 요청
            if (((HttpServletRequest) request).getRequestURI().equals("/api/members/logout")) {
                jwtTokenProvider.logout(token);
            }
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private String removeBearer(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }

}
