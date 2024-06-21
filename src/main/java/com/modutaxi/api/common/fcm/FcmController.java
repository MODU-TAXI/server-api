package com.modutaxi.api.common.fcm;

import com.modutaxi.api.common.auth.CurrentMember;
import com.modutaxi.api.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/fcm")
public class FcmController {

    private final FcmService fcmService;

    /**
     * FCM 구독하기
     */
    @Operation(summary = "FCM 구독")
    @GetMapping("/subscribe")
    public void subscribeTopic(@CurrentMember Member member,
                               @RequestParam Long roomId) {
        fcmService.subscribe(member.getId(), roomId);
    }

    @Operation(summary = "FCM 구독 취소")
    @GetMapping("/unsubscribe")
    public void unsubscribeTopic(@CurrentMember Member member,
                                 @RequestParam Long roomId) {
        fcmService.unsubscribe(member.getId(), roomId);
    }

}
