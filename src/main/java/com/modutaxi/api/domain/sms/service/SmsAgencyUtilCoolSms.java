package com.modutaxi.api.domain.sms.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.domain.mail.service.MailService;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Balance;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.MessageListRequest;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.MessageListResponse;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Deprecated
public class SmsAgencyUtilCoolSms implements SmsAgencyUtil {
    private final MailService mailService;
    private final DefaultMessageService messageService;
    private final Integer checkBalanceThreshold;


    public SmsAgencyUtilCoolSms(
        MailService mailService,
        @Value("${api.cool-sms.api-key}") String apiKey,
        @Value("${api.cool-sms.api-secret}") String apiSecretKey,
        @Value("${api.cool-sms.checkBalanceThreshold}") Integer checkBalanceThreshold
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
        this.checkBalanceThreshold = checkBalanceThreshold;
        this.mailService = mailService;
    }

    @Override
    public void getPrevMessage(String sender, String phoneNumber, String messageId) {
        MessageListRequest request = new MessageListRequest();
        request.setMessageId(messageId);
        request.setLimit(1);
        request.setTo(phoneNumber);
        request.setFrom(sender);
        request.setType("SMS");
        request.getStartDate();
        MessageListResponse response = messageService.getMessageList(request);
        String status = response.getMessageList().get(messageId).getStatus();
        if (status.equals("PENDING")) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_SENDING);
        } else if (status.equals("SENDING")) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_SENDING);
        } else if (status.equals("COMPLETE")) {
        } else if (status.equals("FAILED")) {
            log.warn("[COOL SMS] 메세지 발송에 실패하여 재시도 합니다.");
        }
    }

    @Override
    public String sendOne(String sender, String phoneNumber, String text) {
        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phoneNumber);
        message.setText(text);
        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        return response.getMessageId();
    }

    @Override
    public void checkBalance() {
        Balance balance = messageService.getBalance();
        try {
            if (balance.getPoint().longValue() <= checkBalanceThreshold) {
                mailService.sendCoolSmsBalanceMessage(balance.getPoint().longValue());
                log.warn(String.format("[COOL SMS] 잔액이 %s원 남았습니다.", balance));
            }
        } catch (NullPointerException e) {
            log.warn("[COOL SMS] 남은 요금을 확인할 수 없습니다.");
        }
    }
}
