package com.modutaxi.api.domain.account.controller;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.account.service.UpdateAccountService;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
@Tag(name = "계좌", description = "계좌 API")
public class UpdateAccountController {

    private final UpdateAccountService updateAccountService;

    /**
     * [DELETE] 계좌 삭제
     */
    @Operation(summary = "계좌 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Integer> delete(
        @CurrentMember Member member,
        @PathVariable("id") Long id) {
        updateAccountService.delete(member, id);
        return ResponseEntity.ok(200);
    }
}
