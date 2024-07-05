package com.modutaxi.api.common.auth.oauth.apple.repository;

import com.modutaxi.api.common.auth.oauth.apple.entity.AppleRefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AppleRefreshTokenMongoRepository extends MongoRepository<AppleRefreshToken, String> {
}
