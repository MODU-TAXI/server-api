package com.modutaxi.api.domain.account.service;

import static com.modutaxi.api.domain.account.mapper.AccountMapper.toDto;
import static com.modutaxi.api.domain.account.mapper.AccountMapper.toEntity;

import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountResponse;
import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.account.repository.AccountRepository;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterAccountService {

    private final AccountRepository accountRepository;
    private final MemberRepository memberRepository;

    public AccountResponse register(Member member, String accountNumber, Bank bank) {
        Account account = accountRepository.findByMemberAndAccountNumberAndBank(
            member, accountNumber, bank).orElse(null);
        // 계좌가 이미 존재하지 않는다면 등록
        if(account == null) {
            account = toEntity(member, accountNumber, bank);
            accountRepository.save(account);

            member.addAccount(account);
            memberRepository.save(member);
        }
        return toDto(account);
    }

}
