package com.modutaxi.api.domain.account.service;

import static com.modutaxi.api.domain.account.mapper.AccountMapper.toDto;

import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountResponse;
import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountsResponse;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.member.entity.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAccountService {

    private final AccountRepository accountRepository;

    public AccountsResponse getAccounts(Member member) {
        List<Account> accounts = accountRepository.findAllByMemberAndStatusTrue(member);
        List<AccountResponse> accountResponses = new ArrayList<>();
        accounts.forEach(
            account -> accountResponses.add(toDto(account))
        );
        return new AccountsResponse(accountResponses);
    }
}
