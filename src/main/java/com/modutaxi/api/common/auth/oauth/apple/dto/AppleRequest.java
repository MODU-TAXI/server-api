package com.modutaxi.api.common.auth.oauth.apple.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class AppleRequest {
    @Getter
    @NoArgsConstructor
    @ToString
    public static class StsPayload {
        private String payload;
    }

    /**
     * <h3>Apple Server To Server Request</h3>
     * <h6>iss</h6>
     * 발급자 식별자
     * <h6>aud</h6>
     * 수신자 식별자
     * <h6>iat</h6>
     * 발급 시간
     * <h6>jti</h6>
     * JWT ID
     * <h6>events</h6>
     * 이벤트 정보
     */
    @Getter
    @NoArgsConstructor
    @ToString
    public static class StsRequest {
        private String iss;
        private String aud;
        private long iat;
        private String jti;
        private String events;
    }

    /**
     * <h3>Apple Server To Server Request Events</h3>
     * <h6>type</h6>
     * email-enabled : Hide-My-Email을 통해 포워딩 이메일 활성화<br>
     * email-disabled : Hide-My-Email을 통해 포워딩 이메일 비활성화<br>
     * consent-revoked : 설정 앱을 통한 애플리케이션 회원 탈퇴<br>
     * account-delete : 애플계정 자체 삭제
     * <h6>sub</h6>
     * 이벤트를 발생시킨 애플 계정의 고유 식별자
     * <h6>email</h6>
     * 이벤트를 발생시킨 애플 계정의 이메일 주소
     * <h6>is_private_email</h6>
     * 이벤트를 발생시킨 애플 계정의 이메일 주소가 비공개인지 여부
     * <h6>event_time</h6>
     * 이벤트 발생 시간
     */
    @Getter
    @NoArgsConstructor
    @ToString
    public static class Events {
        private String type;
        private String sub;
        private String email;
        private String is_private_email;
        private long event_time;
    }
}
