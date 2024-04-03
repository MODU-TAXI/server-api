package com.modutaxi.api.domain.member.repository;

import java.util.concurrent.TimeUnit;

public interface RedisSnsIdRepository {
    String save(String snsId);
    String findById(String key);
}
