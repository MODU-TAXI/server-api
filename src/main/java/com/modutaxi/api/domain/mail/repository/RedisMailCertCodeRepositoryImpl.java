package com.modutaxi.api.domain.mail.repository;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import com.modutaxi.api.domain.mail.dao.CertCodeEntity;
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
public class RedisMailCertCodeRepositoryImpl extends BaseRedisRepository implements Serializable, RedisMailCertCodeRepository {

    private final RedisTemplate<String, CertCodeEntity> redisTemplate;
    private ValueOperations<String, CertCodeEntity> valueOperations;
    @Value("${mail.cert-mail-expire-minutes}")
    private Integer certMailExpireMinutes;

    @PostConstruct
    protected void init() {
        classInstance = RedisMailCertCodeRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public Boolean save(Long memberId, String emailAddress, String certificationCode) {
        String key = generateGlobalKey(memberId.toString());
        Duration timeoutDuration = Duration.ofMinutes(certMailExpireMinutes);
        CertCodeEntity certCodeEntity = new CertCodeEntity(certificationCode, emailAddress, LocalDateTime.now());
        valueOperations.set(key, certCodeEntity, timeoutDuration);
        return true;
    }

    @Override
    public CertCodeEntity findById(Long memberId) {
        String key = generateGlobalKey(memberId.toString());
        return valueOperations.get(key);
    }

    @Override
    public Boolean deleteById(Long memberId) {
        String key = generateGlobalKey(memberId.toString());
        return redisTemplate.delete(key);
    }
}
