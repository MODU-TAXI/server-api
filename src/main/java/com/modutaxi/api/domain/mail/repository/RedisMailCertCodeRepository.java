package com.modutaxi.api.domain.mail.repository;

public interface RedisMailCertCodeRepository {
    Boolean save(String signinKey, String certificationCode);
    String findById(String signinKey);
    Boolean deleteById(String signinKey);
}
