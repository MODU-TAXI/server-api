package com.modutaxi.api.domain.sms.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.sms.dao.SmsCertCodeEntity;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;

@Repository
@RequiredArgsConstructor
public class RedisSmsCertificationCodeRepositoryImpl extends BaseRedisRepository implements Serializable, RedisSmsCertificationCodeRepository {
    private final RedisTemplate<String, SmsCertCodeEntity> redisTemplate;
    private ValueOperations<String, SmsCertCodeEntity> valueOperations;
    @Value("${sms.cert-expire-minutes}")
    private Integer certSmsExpireMinutes;

    @PostConstruct
    protected void init() {
        classInstance = RedisSmsCertificationCodeRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Boolean save(String signupKey, String phoneNumber, String certificationCode, String messageId) {
        String key = generateGlobalKey(signupKey);
        Duration timeoutDuration = Duration.ofMinutes(certSmsExpireMinutes);
        SmsCertCodeEntity smsCertCodeEntity = new SmsCertCodeEntity(certificationCode, phoneNumber, LocalDateTime.now(), messageId);
        valueOperations.set(key, smsCertCodeEntity, timeoutDuration);
        return true;
    }

    @Override
    public SmsCertCodeEntity findById(String signupKey) {
        return valueOperations.get(generateGlobalKey(signupKey));
    }

    @Override
    public SmsCertCodeEntity findAndDeleteById(String signupKey) {
        return valueOperations.getAndDelete(generateGlobalKey(signupKey));
    }
}