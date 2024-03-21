package com.modutaxi.api.common.config.redis;

public abstract class BaseRedisRepository {
    protected Class<?> classInstance;
    protected String generateGlobalKey(String localKey) {
        return classInstance.getSimpleName().substring(0,classInstance.getSimpleName().length()-14) + "-" + localKey;
    }
}