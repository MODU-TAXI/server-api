package com.modutaxi.api.common.auth.oauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IdTokenRequest {
    @JsonProperty("client_id")
    private String client_id;
    @JsonProperty("client_secret")
    private String client_secret;
    @JsonProperty("code")
    private String code;
    @JsonProperty("grant_type")
    private String grant_type;
    @JsonProperty("refresh_token")
    private String refresh_token;
    @JsonProperty("redirect_uri")
    private String redirect_uri;
}