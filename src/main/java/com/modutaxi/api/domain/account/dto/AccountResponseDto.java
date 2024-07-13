package com.modutaxi.api.domain.account.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class AccountResponseDto {

    @Getter
    @AllArgsConstructor
    @ToString
    public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private Bank bank;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class AccountsResponse {
        private List<AccountResponse> accounts;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class DeleteAccountResponse {
        private boolean isDeleted;
    }
}
