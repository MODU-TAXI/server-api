package com.modutaxi.api.domain.account.repository;

import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberAndAccountNumberAndBank(
        @Param("member") Member member,
        @Param("accountNumber") String accountNumber,
        @Param("bank")Bank bank);
}
