package com.modutaxi.api.domain.apple;

import com.modutaxi.api.domain.apple.AppleRequest.StsPayload;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/apple")
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
    @PostMapping("/sts")
    public void appleServerToServer(@RequestBody StsPayload payload) {
        appleService.appleServerToServer(payload);
    }
}
