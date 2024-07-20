package com.modutaxi.api.domain.account.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AccountResponseDto {

    @Getter
    @AllArgsConstructor
    public static class AccountResponse {
        private Long id;
        private String accountNumber;
        private Bank bank;
        private String ownerName;
    }

    @Getter
    @AllArgsConstructor
    public static class AccountsResponse {
        private List<AccountResponse> accounts;
    }

    @Getter
    @AllArgsConstructor
    public static class DeleteAccountResponse {
        private boolean isDeleted;
    }
}
