package com.modutaxi.api.common.config.redis.redisExample;

public interface RedisExampleRepository {
    String save(RedisExampleDomain redisExampleDomain);
    RedisExampleDomain findById(String key);
}
