package com.modutaxi.api.domain.mail.repository;

public interface RedisMailCertCodeRepository {
    Boolean save(String signinKey, String certificationCode);
}
