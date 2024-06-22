package com.modutaxi.api.common.auth.oauth.apple.controller;

import com.modutaxi.api.common.auth.oauth.apple.dto.AppleRequest.StsPayload;
import com.modutaxi.api.common.auth.oauth.apple.service.AppleService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppleController {
    private final AppleService appleService;

    /**
     * <h3>Apple Server To Server</h3>
     * 애플 서버에서 호출하는 API
     *
     * @param payload
     */
    @Hidden
    @PostMapping("/api/apple/sts")
    public void appleServerToServer(@RequestBody StsPayload payload) {
        appleService.appleServerToServer(payload);
    }
}
