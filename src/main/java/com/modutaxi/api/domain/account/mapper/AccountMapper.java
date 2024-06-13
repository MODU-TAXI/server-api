package com.modutaxi.api.domain.account.mapper;

import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountResponse;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.member.entity.Member;

public class AccountMapper {

    public static Account toEntity(Member member, String accountNumber, Bank bank) {
        return Account.builder()
            .member(member)
            .accountNumber(accountNumber)
            .bank(bank)
            .build();
    }

    public static AccountResponse toDto(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(), account.getBank());
    }
}
