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
        // 헤더에 Authorization이 아닌 refreshToken이 있다면 refreshToken 사용
        String refreshToken = jwtTokenProvider.resolveRefreshToken((HttpServletRequest) request);
        String token = jwtTokenProvider.resolveAccessToken((HttpServletRequest) request);
        // 헤더에 refreshToken을 보냈다면 refreshToken 유효성 검사 및 authentication 세팅
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) refreshToken = refreshToken.substring(7);
        if (refreshToken != null && ((HttpServletRequest) request).getRequestURI()
                .equals("/refresh") && jwtTokenProvider.validateRefreshToken(refreshToken)) {
            if (refreshToken.startsWith("Bearer ")) refreshToken = refreshToken.substring(7);
            Authentication authentication = jwtTokenProvider.getRefreshAuthentication(refreshToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        if (token!= null && token.startsWith("Bearer ")) token = token.substring(7);
        // 헤더에 aceessToken을 보냈다면 accessToken 유효성 검사 및 authentication 세팅
        else if (token != null && jwtTokenProvider.validateAccessToken(token)) {
            Authentication authentication = jwtTokenProvider.getAccessAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
}
