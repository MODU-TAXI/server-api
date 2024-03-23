package com.modutaxi.api.domain.member.repository;

import java.util.concurrent.TimeUnit;

public interface RedisSnsIdRepository {
    public String save(String snsId, int timeout, TimeUnit timeunit);
    public String findById(String key);
}
