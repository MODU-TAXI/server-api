package com.modutaxi.api.common.fcm;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
@RequiredArgsConstructor
public class RedisFcmRepositoryImpl extends BaseRedisRepository implements Serializable, RedisFcmRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    @PostConstruct
    protected void init() {
        classInstance = com.modutaxi.api.domain.member.repository.RedisRTKRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public void save(Long memberId, String fcmToken) {
        String key = generateGlobalKey(memberId.toString());
        valueOperations.set(key, fcmToken);
    }

    @Override
    public String findById(Long memberId) {
        return valueOperations.get(generateGlobalKey(memberId.toString()));
    }
}