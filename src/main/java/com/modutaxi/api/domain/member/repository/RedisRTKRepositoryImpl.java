package com.modutaxi.api.domain.member.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRTKRepositoryImpl extends BaseRedisRepository implements Serializable, RedisRTKRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    public static final int REFRESH_TOKEN_VALID_DAYS = 7;

    @PostConstruct
    protected void init() {
        classInstance = RedisRTKRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Boolean save(String refreshToken, Long memberId) {
        String key = generateGlobalKey(refreshToken);
        valueOperations.set(key, memberId.toString());
        redisTemplate.expire(key, REFRESH_TOKEN_VALID_DAYS, TimeUnit.DAYS);
        return true;
    }

    @Override
    public String findAndDeleteById(String key) {
        return valueOperations.getAndDelete(generateGlobalKey(key));
    }
}
