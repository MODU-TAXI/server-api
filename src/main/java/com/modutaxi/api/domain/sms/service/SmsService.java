package com.modutaxi.api.domain.sms.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.common.util.cert.CertificationCodeUtil;
import com.modutaxi.api.domain.member.repository.RedisSnsIdRepository;
import com.modutaxi.api.domain.sms.dao.SmsCertCodeEntity;
import com.modutaxi.api.domain.sms.repository.RedisSmsCertificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SmsService {
    private final RedisSnsIdRepository redisSnsIdRepository;
    private final RedisSmsCertificationCodeRepository redisSmsCertificationCodeRepository;
    private final SmsAgencyUtil smsAgencyUtil;
    @Value("${sms.cert-sms-sender}")
    private String sender;
    @Value("${sms.cert-code-length}")
    private Integer certCodeLength;
    @Value("${sms.cert-sms-restriction-seconds}")
    private Integer certSmsRestrictionSeconds;

    public Boolean sendCertificationCode(String signupKey, String phoneNumber) {
        checkSignupKey(signupKey);
        phoneNumber = checkPhoneNumberPattern(phoneNumber);
        SmsCertCodeEntity smsCertCodeEntity = redisSmsCertificationCodeRepository.findById(signupKey);
        if (smsCertCodeEntity != null) {
            smsAgencyUtil.getPrevMessage(sender, phoneNumber, smsCertCodeEntity.getMessageId());
            if (smsCertCodeEntity.getPhoneNumber().equals(phoneNumber) &&
                smsCertCodeEntity.getCreatedAt().plusSeconds(certSmsRestrictionSeconds).isAfter(LocalDateTime.now())) {
                throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_ALREADY_SENT);
            }
        }
        String certificationCode = CertificationCodeUtil.generateCertificationCode(certCodeLength);
        String messageId = smsAgencyUtil.sendOne(sender, phoneNumber, String.format("[모두의택시] 인증번호는 [%s]입니다.", certificationCode));
        redisSmsCertificationCodeRepository.save(signupKey, phoneNumber, certificationCode, messageId);
        smsAgencyUtil.checkBalance();
        return true;
    }

    private String checkPhoneNumberPattern(String phoneNumber) {
        String REGEXP_ONLY_NUM = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})+$";
        if (!phoneNumber.matches(REGEXP_ONLY_NUM)) {
            throw new BaseException(SmsErrorCode.INVALID_PHONE_NUMBER_PATTERN);
        }
        String[] numbers = phoneNumber.split("-");
        return numbers[0] + numbers[1] + numbers[2];
    }

    private void checkSignupKey(String signupKey) {
        if (redisSnsIdRepository.findByKey(signupKey) == null) {
            throw new BaseException(AuthErrorCode.INVALID_SNS_ID_KEY);
        }
    }

    public Boolean checkSmsCertificationCode(String signupKey, String phoneNumber, String certificationCode) {
        checkSignupKey(signupKey);
        phoneNumber = checkPhoneNumberPattern(phoneNumber);
        checkCertificationCodePattern(certificationCode);
        SmsCertCodeEntity smsCertCodeEntity = redisSmsCertificationCodeRepository.findById(signupKey);
        if (smsCertCodeEntity == null) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_EXPIRED);
        }
        if (!smsCertCodeEntity.getPhoneNumber().equals(phoneNumber)) {
            throw new BaseException(SmsErrorCode.PHONE_NUMBER_NOT_MATCH);
        }
        if (!smsCertCodeEntity.getCertificationCode().equals(certificationCode)) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_NOT_MATCH);
        }
        redisSmsCertificationCodeRepository.findAndDeleteById(signupKey);
        return true;
    }

    private void checkCertificationCodePattern(String certificationCode) {
        String REGEXP_ONLY_NUM = String.format("([0-9]{%d})+$", certCodeLength);
        if (!certificationCode.matches(REGEXP_ONLY_NUM)) {
            throw new BaseException(SmsErrorCode.INVALID_CERTIFICATION_CODE_PATTERN);
        }
    }
}
