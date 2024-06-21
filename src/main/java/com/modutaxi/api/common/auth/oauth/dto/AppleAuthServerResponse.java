package com.modutaxi.api.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class AppleAuthServerResponse {
    @Getter
    public static class AppleSocialTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("expires_in")
        private Long expiresIn;
        @JsonProperty("id_token")
        private String idToken;
        @JsonProperty("refresh_token")
        private String refreshToken;
        @JsonProperty("token_type")
        private String tokenType;
    }
}
