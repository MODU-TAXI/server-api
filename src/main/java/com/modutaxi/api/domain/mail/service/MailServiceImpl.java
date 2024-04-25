package com.modutaxi.api.domain.mail.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.domain.mail.dao.CertCodeEntity;
import com.modutaxi.api.domain.mail.repository.RedisMailCertCodeRepository;
import com.modutaxi.api.domain.mail.vo.MailDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailUtil mailUtil;
    private final RedisMailCertCodeRepository redisMailCertCodeRepository;
    @Value("${mail.cert-mail-restriction-seconds}")
    private Integer certMailRestrictionSeconds;

    @Override
    public Boolean sendEmailCertificationMail(Long memberId, String emailAddress) {
        CertCodeEntity certCodeEntity = redisMailCertCodeRepository.findById(memberId);
        if (certCodeEntity != null
            && certCodeEntity.getEmailAddress().equals(emailAddress)
            && certCodeEntity.getCreatedAt().plusSeconds(certMailRestrictionSeconds).isAfter(LocalDateTime.now())
        ) {
            throw new BaseException(MailErrorCode.TOO_MANY_CERTIFICATION_CODE_REQUEST);
        }
        String certificationCode = mailUtil.sendEmailCertificationHtmlMail(emailAddress);
        return redisMailCertCodeRepository.save(memberId, emailAddress, certificationCode);
    }

    @Override
    public Boolean checkMailDomain(String emailAddress) {
        String domainAddress = emailAddress.split("@")[1];
        return MailDomain.isExistDomain(domainAddress);
    }

    @Override
    public String checkEmailCertificationCode(Long memberId, String certificationCode) {
        CertCodeEntity certCodeEntity = redisMailCertCodeRepository.findById(memberId);
        if (certCodeEntity == null) {
            throw new BaseException(MailErrorCode.CERTIFICATION_CODE_EXPIRED);
        }
        if (!certificationCode.equals(certCodeEntity.getCertificationCode())) {
            throw new BaseException(MailErrorCode.CERTIFICATION_CODE_NOT_MATCH);
        }
        redisMailCertCodeRepository.deleteById(memberId);
        return certCodeEntity.getEmailAddress();
    }

    @Override
    public void sendCoolSmsBalanceMessage(Long balance) {
        mailUtil.sendEmailCoolSmsBalanceMail(balance);
    }
}
