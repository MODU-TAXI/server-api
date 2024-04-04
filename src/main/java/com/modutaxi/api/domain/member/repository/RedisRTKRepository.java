package com.modutaxi.api.domain.member.repository;

public interface RedisRTKRepository {
    Boolean save(String refreshToken, Long memberId);
    String findAndDeleteById(String key);
}
