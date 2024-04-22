package com.modutaxi.api.domain.sms.repository;

import com.modutaxi.api.domain.sms.dao.SmsCertCodeEntity;

public interface RedisSmsCertificationCodeRepository {
    Boolean save(String signupKey, String phoneNumber, String certificationCode, String messageId);
    SmsCertCodeEntity findById(String signupKey);
}
