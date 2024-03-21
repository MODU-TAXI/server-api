package com.modutaxi.api.common.config.redis;

public abstract class BaseRedisRepository {
    protected Class<?> classInstance;

    /**
     * @PostConstruct 어노테이션을 붙여주세요.
     * classInstance 를 사용하는 RepositoryImpl 클래스로 저장해주세요.
     * 사용할 redis operations 을 정의해주세요.
     */
    abstract protected void init();
    protected String generateGlobalKey(String localKey) {
        return classInstance.getSimpleName().substring(0,classInstance.getSimpleName().length()-14) + "-" + localKey;
    }
}