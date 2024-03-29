package com.modutaxi.api.domain.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CertCodeEntity {
    private String certificationCode;
    private String emailAddress;
    private LocalDateTime createdAt;
}
