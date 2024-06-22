package com.modutaxi.api.common.auth.oauth.apple.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "apple")
public class AppleOauthProperties {
    private String client_id;
    private String redirect_uri;
    private String team_id;
    private String key_id;
    private String private_key;
    private String iss;
}
