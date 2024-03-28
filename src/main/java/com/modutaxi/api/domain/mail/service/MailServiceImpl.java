package com.modutaxi.api.domain.mail.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MailErrorCode;
import com.modutaxi.api.domain.mail.repository.RedisMailCertCodeRepository;
import com.modutaxi.api.domain.mail.vo.MailDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final MailUtil mailUtil;
    private final RedisMailCertCodeRepository redisMailCertCodeRepository;

    @Override
    public Boolean sendEmailCertificationMail(Long memberId, String emailAddress) {
        String certificationCode = mailUtil.sendEmailCertificationHtmlMail(emailAddress);
        return redisMailCertCodeRepository.save(memberId, certificationCode);
    }

    @Override
    public Boolean checkMailDomain(String emailAddress) {
        String domainAddress = emailAddress.split("@")[1];
        return MailDomain.isExistDomain(domainAddress);
    }

    @Override
    public Boolean checkEmailCertificationCode(Long memberId, String certificationCode) {
        String savedCertificationCode = redisMailCertCodeRepository.findById(memberId);
        if(savedCertificationCode == null) {
            throw new BaseException(MailErrorCode.CERTIFICATION_CODE_EXPIRED);
        }
        if(!certificationCode.equals(savedCertificationCode)) {
            throw new BaseException(MailErrorCode.CERTIFICATION_CODE_NOT_MATCH);
        }
        redisMailCertCodeRepository.deleteById(memberId);
        return true;
    }
}
