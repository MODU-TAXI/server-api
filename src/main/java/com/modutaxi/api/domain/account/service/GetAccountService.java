package com.modutaxi.api.domain.account.service;

import static com.modutaxi.api.domain.account.mapper.AccountMapper.toDto;

import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountResponse;
import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountsResponse;
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
        List<AccountResponse> accounts = new ArrayList<>();
        member.getAccounts().forEach(
            account -> accounts.add(toDto(account))
        );
        return new AccountsResponse(accounts);
    }
}
