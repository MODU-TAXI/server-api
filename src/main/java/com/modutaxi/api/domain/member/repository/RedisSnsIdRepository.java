package com.modutaxi.api.domain.member.repository;


public interface RedisSnsIdRepository {
    String save(String snsId);
    String findAndDeleteById(String key);
}
