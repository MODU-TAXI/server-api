package com.modutaxi.api.common.auth.oauth.apple.repository;

import com.modutaxi.api.common.auth.oauth.apple.entity.AppleRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppleRefreshTokenRepository extends JpaRepository<AppleRefreshToken, String> {
}
