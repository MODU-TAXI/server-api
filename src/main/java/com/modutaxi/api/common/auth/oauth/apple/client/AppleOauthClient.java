package com.modutaxi.api.common.auth.oauth.apple.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleAuthServerRequest.IdTokenRequest;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleAuthServerRequest.RevokeTokenRequest;
import com.modutaxi.api.common.auth.oauth.apple.dto.AppleAuthServerResponse.AppleSocialTokenResponse;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AppleOauthClient {
    public AppleSocialTokenResponse generateAndValidateToken(IdTokenRequest idTokenRequest) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://appleid.apple.com/auth/token");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("client_id", idTokenRequest.getClient_id()));
        nvps.add(new BasicNameValuePair("client_secret", idTokenRequest.getClient_secret()));
        nvps.add(new BasicNameValuePair("code", idTokenRequest.getCode()));
        nvps.add(new BasicNameValuePair("grant_type", idTokenRequest.getGrant_type()));
        nvps.add(new BasicNameValuePair("refresh_token", idTokenRequest.getRefresh_token()));
        nvps.add(new BasicNameValuePair("redirect_uri", idTokenRequest.getRedirect_uri()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("Apple Token Request Body 인코딩 실패 : {}", idTokenRequest);
            throw new BaseException(AuthErrorCode.APPLE_LOGIN_ERROR);
        }
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("Apple Token 요청 실패 : {}", idTokenRequest);
            throw new BaseException(AuthErrorCode.APPLE_LOGIN_ERROR);
        }
        ObjectMapper objectMapper = new ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        AppleSocialTokenResponse responseBody;
        try {
            responseBody = objectMapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), AppleSocialTokenResponse.class);
        } catch (IOException e) {
            log.error("Apple Token Payload 디코딩 실패 : {}", idTokenRequest);
            throw new BaseException(AuthErrorCode.APPLE_LOGIN_ERROR);
        }
        return responseBody;
    }

    public void revokeToken(RevokeTokenRequest revokeTokenRequest) throws BaseException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://appleid.apple.com/auth/revoke");
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("client_id", revokeTokenRequest.getClient_id()));
        nvps.add(new BasicNameValuePair("client_secret", revokeTokenRequest.getClient_secret()));
        nvps.add(new BasicNameValuePair("token", revokeTokenRequest.getToken()));
        nvps.add(new BasicNameValuePair("token_type_hint", revokeTokenRequest.getToken_type_hint()));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("Apple Revoke Token Request Body 인코딩 실패 : {}", revokeTokenRequest);
            throw new BaseException(AuthErrorCode.APPLE_REVOKE_ERROR);
        }
        try {
            httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("Apple Revoke Token 요청 실패 : {}", revokeTokenRequest);
            throw new BaseException(AuthErrorCode.APPLE_REVOKE_ERROR);
        }
    }
}
