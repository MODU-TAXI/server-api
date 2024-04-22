package com.modutaxi.api.domain.sms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

public interface SmsService {
    Boolean sendCertificationCode(String signupKey, String phoneNumber);
}
