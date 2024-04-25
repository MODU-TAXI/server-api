package com.modutaxi.api.domain.sms.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SmsCertCodeEntity {
    private String certificationCode;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String messageId;
}
