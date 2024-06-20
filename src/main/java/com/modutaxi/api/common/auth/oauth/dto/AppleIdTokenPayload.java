package com.modutaxi.api.common.auth.oauth.dto;

import lombok.Getter;

@Getter
public class AppleIdTokenPayload {
    private String iss; // 발급자
    private String aud;
    private String exp; // 만료 시간
    private String iat; // 발급 시간
    private String sub; // 사용자 고유 번호
    private String c_hash; // 해시값
    private String nonce;
    private String auth_time; // 인증 시간
    private String nonce_supported; // nonce 지원 여부
}