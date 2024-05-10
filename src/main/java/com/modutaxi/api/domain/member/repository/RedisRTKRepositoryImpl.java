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

    @PostConstruct
    protected void init() {
        classInstance = RedisRTKRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void save(Long memberId, String refreshToken, Long duration) {
        String key = generateGlobalKey(memberId.toString());
        valueOperations.set(key, refreshToken, duration, TimeUnit.MILLISECONDS);
    }

    @Override
    public String findAndDeleteById(String memberId) {
        return valueOperations.getAndDelete(generateGlobalKey(memberId));
    }
}
