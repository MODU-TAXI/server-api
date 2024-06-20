package com.modutaxi.api.common.auth.oauth;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import com.modutaxi.api.common.auth.oauth.client.AppleOauthClient;
import com.modutaxi.api.common.auth.oauth.dto.AppleIdTokenPayload;
import com.modutaxi.api.common.auth.oauth.dto.AppleSocialTokenResponse;
import com.modutaxi.api.common.auth.oauth.dto.IdTokenRequest;
import com.modutaxi.api.common.auth.oauth.vo.AppleOauthProperties;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PrivateKey;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class SocialLoginService {
    private final AppleOauthClient appleOauthClient;
    private final AppleOauthProperties appleOauthProperties;

    public String getKaKaoSnsId(String accessToken) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        StringBuilder result = getKaKaoResponse(accessToken, requestUrl);
        return new JsonParser().parse(result.toString()).
                getAsJsonObject().get("id").getAsString();
    }

    public StringBuilder getKaKaoResponse(String accessToken, String requestUrl) throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.GET.name());
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result;
    }

    public String getAppleSub(String authorizationCode) {
        AppleIdTokenPayload appleIdTokenResponse = getAppleIdTokenResponse(authorizationCode);
        return appleIdTokenResponse.getSub();
    }

    private AppleIdTokenPayload getAppleIdTokenResponse(String authorizationCode) {
        AppleSocialTokenResponse response = appleOauthClient.generateAndValidateToken(new IdTokenRequest(
            appleOauthProperties.getClient_id(),
            generateClientSecret(),
            authorizationCode,
            "authorization_code",
            null,
            appleOauthProperties.getRedirect_uri()
        ));
        return decodePayload(response.getIdToken(), AppleIdTokenPayload.class);
    }

    private String generateClientSecret() {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);
        return Jwts.builder()
            .setHeaderParam("alg", "ES256")
            .setHeaderParam("kid", appleOauthProperties.getKey_id())
            .setIssuer(appleOauthProperties.getTeam_id())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(java.sql.Timestamp.valueOf(expiration))
            .setAudience("https://appleid.apple.com")
            .setSubject(appleOauthProperties.getClient_id())
            .signWith(SignatureAlgorithm.ES256, getPrivateKey())
            .compact();
    }

    private PrivateKey getPrivateKey() {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(appleOauthProperties.getPrivate_key());
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(privateKeyBytes);
            return converter.getPrivateKey(privateKeyInfo);
        } catch (Exception e) {
            throw new RuntimeException("Error converting private key from String", e);
        }
    }

    private <T> T decodePayload(String token, Class<T> targetClass) {
        String[] toeknParts = token.split("\\.");
        String payloadJWT = toeknParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}
