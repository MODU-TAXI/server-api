package com.modutaxi.api.domain.account.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.PaymentErrorCode;
import com.modutaxi.api.domain.account.dto.AccountResponseDto.DeleteAccountResponse;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.member.entity.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateAccountService {

    private final AccountRepository accountRepository;

    public DeleteAccountResponse delete(Member member, Long id) {
        Account account = accountRepository.findByIdAndMember(id, member)
            .orElseThrow(() -> new BaseException(PaymentErrorCode.INVALID_ACCOUNT));

        accountRepository.delete(account);

        return new DeleteAccountResponse(true);
    }
}
