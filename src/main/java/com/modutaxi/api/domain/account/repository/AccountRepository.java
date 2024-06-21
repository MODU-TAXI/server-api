package com.modutaxi.api.domain.account.repository;

import com.modutaxi.api.domain.account.entity.Account;
import com.modutaxi.api.domain.account.entity.Bank;
import com.modutaxi.api.domain.member.entity.Member;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByMemberAndAccountNumberAndBank(
        @Param("member") Member member,
        @Param("accountNumber") String accountNumber,
        @Param("bank") Bank bank);

    @NotNull
    Optional<Account> findById(@NotNull @Param("id") Long id);

    List<Account> findAllByMemberAndStatusTrue(@Param("member") Member member);

    void deleteByMember(@Param("member") Member member);

    void deleteById(@NotNull @Param("id") Long id);
}
