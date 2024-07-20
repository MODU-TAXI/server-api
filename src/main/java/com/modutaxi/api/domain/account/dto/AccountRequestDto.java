package com.modutaxi.api.domain.account.dto;

import com.modutaxi.api.domain.account.entity.Bank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class AccountRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class AccountRequest {
        private String accountNumber;
        private Bank bank;
        private String ownerName;
    }
}
