package com.modutaxi.api.common.auth.jwt;

import com.modutaxi.api.common.auth.PrincipalDetails;
import com.modutaxi.api.common.auth.PrincipalDetailsService;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.TokenResponse;
import com.modutaxi.api.domain.member.repository.RedisATKRepositoryImpl;
import com.modutaxi.api.domain.member.repository.RedisRTKRepositoryImpl;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String AUTHORIZATION_HEADER = "Authorization"; // 액세스 토큰 헤더 key name
    private static final String REFRESH_HEADER = "refreshToken";  // 리프레시 토큰 헤더 key name
    private static final long TOKEN_VALID_TIME = 1000 * 30L;  // 유효기간 1시간
    private static final long REF_TOKEN_VALID_TIME = 1000 * 60L * 60L * 24L * 7L;  // 유효기간 일주일

    private final PrincipalDetailsService principalDetailsService;
    private final RedisRTKRepositoryImpl redisRTKRepository;
    private final RedisATKRepositoryImpl redisATKRepository;

    @Value("${jwt.jwt-key}")
    private String jwtSecretKey;
    @Value("${jwt.refresh-key}")
    private String refreshSecretKey;

    /**
     * 의존성 주입 후 (호출 없어도) 오직 1번만 초기화 수행
     */
    @PostConstruct
    protected void init() {
        jwtSecretKey = Base64.getEncoder().encodeToString(jwtSecretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    /**
     * memberId가 적힌 클레임을 넘겨 받아 Token 생성
     */
    public String generateToken(Claims claims, Long duration, String secretKey) {
        Date now = new Date();
        Date accessTokenExpirationTime = new Date(now.getTime() + duration);

        return Jwts.builder()
                .setClaims(claims)  // 정보 저장
                .setIssuedAt(now)   // 토큰 발행 시간 정보
                .setExpiration(accessTokenExpirationTime)   // 토큰 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey)   // 전자 서명
                .compact();
    }

    /**
     * memberId로 AccessToken, RefreshToken 생성 후 리턴
     */
    public TokenResponse generateToken(Long memberId) {
        Claims claims = Jwts.claims();
        claims.put("memberId", memberId);

        String accessToken = generateToken(claims, TOKEN_VALID_TIME, jwtSecretKey);
        String refreshToken = generateToken(claims, REF_TOKEN_VALID_TIME, refreshSecretKey);

        // 레디스에 저장
        redisRTKRepository.save(memberId, refreshToken, REF_TOKEN_VALID_TIME);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * AccessToken으로 사용자 정보 인증하고 Authentication 객체를 반환하는 함수
     */
    public Authentication getAccessAuthentication(String token) {
        return getAuthentication(getMemberIdByToken(token, jwtSecretKey));
    }

    /**
     * RefreshToken으로 사용자 정보 인증하고 Authentication 객체를 반환하는 함수
     */
    public Authentication getRefreshAuthentication(String token) {
        // refreshToken 에서 memberId 빼내서 redis 검색
        String memberId = getMemberIdByToken(token, refreshSecretKey);
        String refreshToken = redisRTKRepository.findAndDeleteById(memberId);
        if (refreshToken == null || !Objects.equals(token, refreshToken)) { // 검색 실패
            throw new BaseException(AuthErrorCode.EXPIRED_MEMBER_JWT);
        }
        return getAuthentication(memberId);
    }

    /**
     * 추출한 memberId로 사용자 정보를 인증하고 Authentication 객체를 반환하는 함수
     *
     * @param memberId (String)
     * @return 권한과 사용자 정보를 담은 Authentication 객체
     */
    public Authentication getAuthentication(String memberId) {
        try {
            // 헤더에서 추출한 memberId를 실제 DB에서 조회하여 사용자 정보 확인
            PrincipalDetails principalDetails
                    = principalDetailsService.loadUserByUsername(memberId);
            return new UsernamePasswordAuthenticationToken(principalDetails,
                    "", principalDetails.getAuthorities());
        } catch (UsernameNotFoundException exception) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        }
    }

    /**
     * AccessToken 을 검증하는 함수
     */
    public void validateAccessToken(String token) {
        validateToken(jwtSecretKey, token);
    }

    /**
     * RefreshToken 을 검증하는 함수
     */
    public void validateRefreshToken(String token) {
        validateToken(refreshSecretKey, token);
    }

    /**
     * 토큰을 검증하는 함수
     *
     * @param key   jwtSecretKey, refreshSecretKey 둘 중 하나
     * @param token accessToken, refreshToken 둘 중 하나
     */
    public void validateToken(String key, String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            if (validateBlacklist(token)) {
                throw new BaseException(AuthErrorCode.LOGOUT_JWT);
            }
        } catch (SecurityException | MalformedJwtException e) {
            throw new BaseException(AuthErrorCode.INVALID_JWT);
        } catch (ExpiredJwtException e) {
            throw new BaseException(AuthErrorCode.EXPIRED_MEMBER_JWT);
        } catch (UnsupportedJwtException | SignatureException e) {
            throw new BaseException(AuthErrorCode.UNSUPPORTED_JWT);
        } catch (IllegalArgumentException e) {
            throw new BaseException(AuthErrorCode.EMPTY_JWT);
        }
    }

    /**
     * Token 에서 memberId를 추출하는 함수
     *
     * @return memberId (String)
     */
    public String getMemberIdByToken(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).
                getBody().get("memberId").toString();
    }

    /**
     * Token 에서 memberId를 추출하는 함수 (웹소켓용)
     *
     * @return memberId (String)
     */
    public String getMemberIdByToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token).
            getBody().get("memberId").toString();
    }

    /**
     * 헤더에서 AUTHORIZATION_HEADER key의 value에 해당하는 AccessToken을 추출
     *
     * @return AccessToken (String)
     */
    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION_HEADER);
    }

    /**
     * 헤더에서 REFRESH_HEADER key의 value에 해당하는 RefreshToken을 추출
     *
     * @return RefreshToken (String)
     */
    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader(REFRESH_HEADER);
    }

    /**
     * 로그아웃 처리
     */
    public void logout(String token) {
        String memberId = getMemberIdByToken(token, jwtSecretKey);
        // ATK 블랙 리스트 처리
        setBlackList(token);
        // RTK 레디스에서 제거
        redisRTKRepository.findAndDeleteById(memberId);
    }

    /**
     * 해당 token의 남은 시간을 유효 시간으로, 블랙리스트에 토큰 저장
     */
    private void setBlackList(String token) {
        Long expiration = getExpiration(token);
        redisATKRepository.save(token, expiration);
    }

    /**
     * 해당 token 의 남은 유효 시간을 반환
     */
    private Long getExpiration(String token) {
        Date expiredDate = extractClaims(jwtSecretKey, token).getExpiration();
        long now = new Date().getTime();    // 현재 시간
        return (expiredDate.getTime() - now);
    }

    /**
     * key, token 을 이용하여 Claims 추출
     */
    private Claims extractClaims(String key, String token) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    /**
     * ATK 가 블랙리스트에 있는지 확인 후 boolean 반환
     */
    private boolean validateBlacklist(String token) {
        return redisATKRepository.existsById(token);
    }
}
