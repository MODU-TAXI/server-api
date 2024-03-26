package com.modutaxi.api.domain.mail.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisMailCertCodeRepositoryImpl extends BaseRedisRepository implements Serializable, RedisMailCertCodeRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private ValueOperations<String, String> valueOperations;
    @Value("${mail.cert-mail-expire-minutes}")
    private Integer certMailExpireMinutes;

    @PostConstruct
    protected void init() {
        classInstance = RedisMailCertCodeRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }
    @Override
    public Boolean save(String signinKey, String certificationCode) {
        String key = generateGlobalKey(signinKey);
        Duration timeoutDuration = Duration.ofMinutes(certMailExpireMinutes);
        valueOperations.set(key, certificationCode, timeoutDuration);
        return true;
    }
}
