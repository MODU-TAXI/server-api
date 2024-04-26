package com.modutaxi.api.domain.sms.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class SmsCertCodeEntity {
    private String certificationCode;
    private String phoneNumber;
    private LocalDateTime createdAt;
    private String messageId;
}
