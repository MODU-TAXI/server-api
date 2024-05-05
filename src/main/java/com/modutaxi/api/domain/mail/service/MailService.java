package com.modutaxi.api.domain.mail.service;

public interface MailService {
    Boolean sendEmailCertificationMail(Long memberId, String emailAddress);
    Boolean checkMailDomain(String emailAddress);
    String checkEmailCertificationCode(Long memberId, String certificationCode);
    void sendCoolSmsBalanceMessage(Long balance);
    void sendAligoRemainSmsMessage(String smsCnt);
}
