package com.modutaxi.api.domain.sms.service;

public interface SmsAgencyUtil {
    void getPrevMessage(String sender, String phoneNumber, String messageId);

    String sendOne(String sender, String phoneNumber, String text);

    void checkBalance();
}
