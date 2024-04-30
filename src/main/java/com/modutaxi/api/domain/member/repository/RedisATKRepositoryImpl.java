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
public class RedisATKRepositoryImpl extends BaseRedisRepository implements Serializable, RedisATKRepository {

    public static final String REASON = "LOGOUT";

    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    protected void init() {
        classInstance = RedisRTKRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void save(String accessToken, Long duration) {
        String key = generateGlobalKey(accessToken);
        valueOperations.set(key, REASON, duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean existsById(String accessToken) {
        return (valueOperations.get(generateGlobalKey(accessToken)) != null);
    }

}
