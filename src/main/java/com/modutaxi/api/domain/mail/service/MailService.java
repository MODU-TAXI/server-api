package com.modutaxi.api.domain.mail.service;

public interface MailService {
    Boolean sendEmailCertificationMail(Long memberId, String emailAddress);
    Boolean checkMailDomain(String emailAddress);
    Boolean checkEmailCertificationCode(Long memberId, String certificationCode);
}
