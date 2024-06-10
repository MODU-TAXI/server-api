package com.modutaxi.api.domain.account.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class AccountResponseDto {

    @Getter
    @AllArgsConstructor
    public static class AccountResponse {
        private String accountNumber;
        private Bank bank;
    }
}
