package com.modutaxi.api.domain.mail.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@ToString
public class CertCodeEntity {
    private String certificationCode;
    private String emailAddress;
    private LocalDateTime createdAt;
}
