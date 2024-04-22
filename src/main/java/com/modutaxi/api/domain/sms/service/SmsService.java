package com.modutaxi.api.domain.sms.service;

public interface SmsService {
    Boolean sendCertificationCode(String signupKey, String phoneNumber);
    Boolean checkSmsCertificationCode(String signupKey, String phoneNumber, String certificationCode);
}
