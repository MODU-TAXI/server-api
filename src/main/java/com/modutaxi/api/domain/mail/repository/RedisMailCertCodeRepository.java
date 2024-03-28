package com.modutaxi.api.domain.mail.repository;

public interface RedisMailCertCodeRepository {
    Boolean save(Long memberId, String certificationCode);

    String findById(Long memberId);

    Boolean deleteById(Long memberId);
}
