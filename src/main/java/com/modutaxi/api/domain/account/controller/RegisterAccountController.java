package com.modutaxi.api.domain.account.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.account.dto.AccountRequestDto.AccountRequest;
import com.modutaxi.api.domain.account.dto.AccountResponseDto.AccountResponse;
import com.modutaxi.api.domain.account.service.RegisterAccountService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Transactional
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Tag(name = "계좌 등록", description = "계좌 등록 API")
public class RegisterAccountController {

    private final RegisterAccountService registerAccountService;

    /**
     * [POST] 계좌 등록
     */
    @Operation(summary = "계좌 등록")
    @PostMapping("")
    public ResponseEntity<AccountResponse> register(
        @CurrentMember Member member,
        @Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(
            registerAccountService.register(
                member,
                accountRequest.getAccountNumber(),
                accountRequest.getBank()));
    }
}
