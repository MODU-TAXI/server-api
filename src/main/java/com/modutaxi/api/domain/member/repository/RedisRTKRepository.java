package com.modutaxi.api.domain.member.repository;

public interface RedisRTKRepository {
    void save(Long memberId, String refreshToken, Long duration);

    String findAndDeleteById(String memberId);
}
