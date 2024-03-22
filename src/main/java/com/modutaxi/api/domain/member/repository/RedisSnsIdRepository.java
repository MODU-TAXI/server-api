package com.modutaxi.api.domain.member.repository;

public interface RedisSnsIdRepository {
    public String save(String snsId);
    public String findById(String key);
}
