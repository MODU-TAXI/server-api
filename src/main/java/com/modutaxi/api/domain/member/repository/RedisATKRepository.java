package com.modutaxi.api.domain.member.repository;

public interface RedisATKRepository {
    void save(String accessToken, Long duration);

    boolean existsById(String accessToken);

}
