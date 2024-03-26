package com.modutaxi.api.domain.mail.service;

public interface MailService {
    Boolean sendEmailCertificationMail(String signinKey, String emailAddress);
    Boolean checkMailDomain(String emailAddress);
}
