package com.modutaxi.api.domain.mail.repository;

import com.modutaxi.api.domain.mail.dto.CertCodeEntity;

public interface RedisMailCertCodeRepository {
    Boolean save(Long memberId, String emailAddress, String certificationCode);
    CertCodeEntity findById(Long memberId);
    Boolean deleteById(Long memberId);
}
