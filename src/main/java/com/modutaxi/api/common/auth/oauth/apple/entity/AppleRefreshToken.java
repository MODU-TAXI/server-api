package com.modutaxi.api.common.auth.oauth.apple.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AppleRefreshToken {
    @Id
    private String sub;
    private String refresh_token;
}
