package com.modutaxi.api.common.config.redis;

public abstract class BaseRedisRepository {
    protected String globalKeyPrefix;
    protected String generateGlobalKey(String localKey) {
        return globalKeyPrefix + localKey;
    }
}