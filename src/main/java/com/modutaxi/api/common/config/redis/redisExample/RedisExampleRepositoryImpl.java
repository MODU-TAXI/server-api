package com.modutaxi.api.common.config.redis.redisExample;

import com.modutaxi.api.common.config.redis.BaseRedisRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

@Repository
@RequiredArgsConstructor
public class RedisExampleRepositoryImpl extends BaseRedisRepository implements Serializable, RedisExampleRepository {
    private final RedisTemplate<String, RedisExampleDomain> redisTemplate;
    private ValueOperations<String, RedisExampleDomain> valueOperations;

    @PostConstruct
    protected void init() {
        classInstance = RedisExampleRepositoryImpl.class;
        valueOperations = redisTemplate.opsForValue();
    }

    @Override
    public String save(RedisExampleDomain redisExampleDomain) {
        valueOperations.set(generateGlobalKey(redisExampleDomain.getKey()), redisExampleDomain);
        return redisExampleDomain.getKey();
    }

    @Override
    public RedisExampleDomain findById(String key) {
        return valueOperations.get(generateGlobalKey(key));
    }
}
