package com.modutaxi.api.common.auth.oauth.apple.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.auth.oauth.apple.client.AppleOauthClient;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleAuthServerRequest;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleIdTokenPayload;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleRequest.Events;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleRequest.StsPayload;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleRequest.StsRequest;
import com.modutaxi.api.common.auth.oauth.apple.vo.AppleOauthProperties;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import com.modutaxi.api.domain.member.service.GetMemberService;
import com.modutaxi.api.domain.member.service.UpdateMemberService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;


@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {
    private final UpdateMemberService updateMemberService;
    private final GetMemberService getMemberService;
    private final AppleOauthClient appleOauthClient;
    private final AppleOauthProperties appleOauthProperties;

    public void appleServerToServer(StsPayload payload) {
        try {
            Events events = null;
            try {
                events = new ObjectMapper().readValue(decodePayload(payload.getPayload(), StsRequest.class).getEvents(), Events.class);
            } catch (JsonProcessingException e) {
                log.error("Apple Server To Server Error : Object Mapper Error");
            }
            if (events.getType().equals("consent-revoked") || events.getType().equals("account-delete")) {
                updateMemberService.deleteMember(getMemberService.getMemberByAppleSnsId(events.getSub()));
            }
        } catch (BaseException e) {
            log.error("Apple Server To Server Error : {}", e);
        }
    }

    private <T> T decodePayload(String token, Class<T> targetClass) {
        try {
            return (new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
                .readValue(new String(Base64.getDecoder().decode(token.split("\\.")[1])), targetClass);
        } catch (JsonProcessingException e) {
            log.error("Apple Server To Server payload 디코딩 실패 : {}", token);
            throw new BaseException(null); // api 이용자는 Apple 인증서버이므로 에러를 보내지 않아도 됨
        }
    }

    public AppleIdTokenPayload getAppleIdTokenResponse(String authorizationCode) {
        return decodeUrlPayload(appleOauthClient.generateAndValidateToken(
            new AppleAuthServerRequest.IdTokenRequest(
                appleOauthProperties.getClient_id(),
                generateClientSecret(),
                authorizationCode,
                "authorization_code",
                null,
                appleOauthProperties.getRedirect_uri()
            )).getIdToken(), AppleIdTokenPayload.class);
    }

    private String generateClientSecret() {
        return Jwts.builder()
            .setHeaderParam("alg", "ES256")
            .setHeaderParam("kid", appleOauthProperties.getKey_id())
            .setIssuer(appleOauthProperties.getTeam_id())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(java.sql.Timestamp.valueOf(LocalDateTime.now().plusMinutes(5)))
            .setAudience("https://appleid.apple.com")
            .setSubject(appleOauthProperties.getClient_id())
            .signWith(SignatureAlgorithm.ES256, getPrivateKey())
            .compact();
    }

    private PrivateKey getPrivateKey() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleOauthProperties.getPrivate_key());
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return (new JcaPEMKeyConverter().setProvider("BC")).getPrivateKey(privateKeyInfo);
        } catch (PEMException e) {
            log.error("Apple Private Key 생성 실패");
            throw new BaseException(AuthErrorCode.APPLE_LOGIN_ERROR);
        }
    }

    private <T> T decodeUrlPayload(String token, Class<T> targetClass) {
        try {
            return (new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
                .readValue(new String(Base64.getUrlDecoder().decode(token.split("\\.")[1])), targetClass);
        } catch (JsonProcessingException e) {
            log.error("Apple Id Token Payload 디코딩 실패 : {}", token);
            throw new BaseException(AuthErrorCode.APPLE_LOGIN_ERROR);
        }
    }
}
