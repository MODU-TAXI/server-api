package com.modutaxi.api.domain.member.controller;

import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.domain.member.dto.MemberResponseDto.MemberProfileResponse;
import com.modutaxi.api.domain.member.service.GetMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class GetMemberController {

    private final GetMemberService getMemberService;

    /**
     * [GET] 멤버 프로필 조회 /{id}
     */
    @Operation(
        summary = "멤버 프로필 조회",
        description = "멤버 프로필을 조회합니다.<br>" +
            "조회하고 싶은 멤버의 id를 보내주세요."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 조회 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberProfileResponse.class))),
        @ApiResponse(responseCode = "409", description = "프로필 조회 실패", content = @Content(mediaType = "application/json", schema = @Schema(implementation = MemberErrorCode.class), examples = {
            @ExampleObject(name = "MEMBER_001", description = "존재하지 않는 사용자입니다.", value = """
                {
                    "errorCode": "MEMBER_001",
                    "message": "존재하지 않는 사용자입니다."
                }
                """),
        })),
    })
    @GetMapping("/{id}")
    public ResponseEntity<MemberProfileResponse> getMemberProfile(
        @Parameter(description = "조회할 멤버의 id")
        @PathVariable("id") Long id
    ) {
        return ResponseEntity.ok(getMemberService.getMemberProfile(id));
    }
}
