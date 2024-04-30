package com.modutaxi.api.domain.sms.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.common.util.cert.CertificationCodeUtil;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.member.repository.RedisSnsIdRepository;
import com.modutaxi.api.domain.sms.dao.SmsCertCodeEntity;
import com.modutaxi.api.domain.sms.repository.RedisSmsCertificationCodeRepository;
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

import java.time.LocalDateTime;

@Slf4j
@Service
public class SmsServiceCoolSmsImpl implements SmsService {

    private final DefaultMessageService messageService;
    private final String sender;
    private final MailService mailService;
    private final RedisSnsIdRepository redisSnsIdRepository;
    private final RedisSmsCertificationCodeRepository redisSmsCertificationCodeRepository;
    private final Integer certSmsRestrictionSeconds;
    private final Integer certCodeLength;
    private final Integer checkBalanceThreshold;


    public SmsServiceCoolSmsImpl(
            MailService mailService,
            RedisSmsCertificationCodeRepository redisSmsCertificationCodeRepository,
            RedisSnsIdRepository redisSnsIdRepository,
            @Value("${api.cool-sms.api-key}") String apiKey,
            @Value("${api.cool-sms.api-secret}") String apiSecretKey,
            @Value("${api.cool-sms.checkBalanceThreshold}") Integer checkBalanceThreshold,
            @Value("${sms.cert-sms-sender}") String sender,
            @Value("${sms.cert-sms-restriction-seconds}") Integer certSmsRestrictionSeconds,
            @Value("${sms.cert-code-length}") Integer certCodeLength
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
        this.sender = sender;
        this.mailService = mailService;
        this.redisSmsCertificationCodeRepository = redisSmsCertificationCodeRepository;
        this.redisSnsIdRepository = redisSnsIdRepository;
        this.checkBalanceThreshold = checkBalanceThreshold;
        this.certSmsRestrictionSeconds = certSmsRestrictionSeconds;
        this.certCodeLength = certCodeLength;
    }

    public Boolean sendCertificationCode(String signupKey, String phoneNumber) {
        checkSignupKey(signupKey);
        phoneNumber = checkPhoneNumberPattern(phoneNumber);
        SmsCertCodeEntity smsCertCodeEntity = redisSmsCertificationCodeRepository.findById(signupKey);
        if (smsCertCodeEntity != null) {
            getPrevMessage(phoneNumber, smsCertCodeEntity.getMessageId());
            if (smsCertCodeEntity.getPhoneNumber().equals(phoneNumber) &&
                    smsCertCodeEntity.getCreatedAt().plusSeconds(certSmsRestrictionSeconds).isAfter(LocalDateTime.now())) {
                throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_ALREADY_SENT);
            }
        }
        String certificationCode = CertificationCodeUtil.generateCertificationCode(certCodeLength);
        String messageId = sendOne(phoneNumber, String.format("[모두의택시] 인증번호는 [%s]입니다.", certificationCode));
        redisSmsCertificationCodeRepository.save(signupKey, phoneNumber, certificationCode, messageId);
        checkBalance();
        return true;
    }

    private void checkSignupKey(String signupKey) {
        if (redisSnsIdRepository.findByKey(signupKey) == null) {
            throw new BaseException(AuthErrorCode.INVALID_SNS_ID_KEY);
        }
    }

    private String checkPhoneNumberPattern(String phoneNumber) {
        String REGEXP_ONLY_NUM = "^01([0|1|6|7|8|9])-([0-9]{3,4})-([0-9]{4})+$";
        if (!phoneNumber.matches(REGEXP_ONLY_NUM)) {
            throw new BaseException(SmsErrorCode.INVALID_PHONE_NUMBER_PATTERN);
        }
        String[] numbers = phoneNumber.split("-");
        return numbers[0] + numbers[1] + numbers[2];
    }

    private void getPrevMessage(String phoneNumber, String messageId) {
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

    private String sendOne(String phoneNumber, String text) {
        Message message = new Message();
        message.setFrom(sender);
        message.setTo(phoneNumber);
        message.setText(text);
        SingleMessageSentResponse response = messageService.sendOne(new SingleMessageSendingRequest(message));
        return response.getMessageId();
    }

    private void checkBalance() {
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

    public Boolean checkSmsCertificationCode(String signupKey, String phoneNumber, String certificationCode) {
        checkSignupKey(signupKey);
        phoneNumber = checkPhoneNumberPattern(phoneNumber);
        checkCertificationCodePattern(certificationCode);
        SmsCertCodeEntity smsCertCodeEntity = redisSmsCertificationCodeRepository.findById(signupKey);
        if (smsCertCodeEntity == null) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_EXPIRED);
        }
        if (!smsCertCodeEntity.getPhoneNumber().equals(phoneNumber)) {
            throw new BaseException(SmsErrorCode.PHONE_NUMBER_NOT_MATCH);
        }
        if (!smsCertCodeEntity.getCertificationCode().equals(certificationCode)) {
            throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_NOT_MATCH);
        }
        redisSmsCertificationCodeRepository.findAndDeleteById(signupKey);
        return true;
    }

    private void checkCertificationCodePattern(String certificationCode) {
        String REGEXP_ONLY_NUM = String.format("([0-9]{%d})+$", certCodeLength);
        if (!certificationCode.matches(REGEXP_ONLY_NUM)) {
            throw new BaseException(SmsErrorCode.INVALID_CERTIFICATION_CODE_PATTERN);
        }
    }
}
