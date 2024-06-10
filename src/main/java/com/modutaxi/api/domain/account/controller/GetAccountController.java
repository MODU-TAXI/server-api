package com.modutaxi.api.domain.account.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountsResponse;
import com.modutaxi.api.domain.account.service.GetAccountService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Tag(name = "계좌 조회", description = "계좌 조회 API")
public class GetAccountController {

    private final GetAccountService getAccountService;

    /**
     * [GET] 계좌 목록 조회
     */
    @Operation(summary = "계좌 목록 조회")
    @GetMapping("")
    public ResponseEntity<AccountsResponse> getMyAccounts(
        @CurrentMember Member member) {
        return ResponseEntity.ok(getAccountService.getAccounts(member));
    }
}
