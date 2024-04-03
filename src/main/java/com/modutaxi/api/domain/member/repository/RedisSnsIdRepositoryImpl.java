package com.modutaxi.api.domain.member.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.common.config.redis.redisExample.RedisExampleRepositoryImpl;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisSnsIdRepositoryImpl extends BaseRedisRepository implements Serializable, RedisSnsIdRepository {
    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;

    public static final int SNS_ID_VALID_HOURS = 1;

    @PostConstruct
    protected void init() {
        classInstance = RedisExampleRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public String save(String snsId) {
        UUID randomUUID = UUID.randomUUID();
        String key = generateGlobalKey(randomUUID.toString());
        valueOperations.set(key, snsId);
        redisTemplate.expire(key, SNS_ID_VALID_HOURS, TimeUnit.HOURS);
        return randomUUID.toString();
    }

    @Override
    public String findById(String key) {
        return valueOperations.getAndDelete(generateGlobalKey(key));
    }
}