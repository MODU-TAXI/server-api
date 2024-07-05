package com.modutaxi.api.common.auth.oauth.apple.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@Document(collection = "apple-refresh-token")
public class AppleRefreshToken implements Serializable {
    @Id
    private String sub;
    private String refresh_token;
}
