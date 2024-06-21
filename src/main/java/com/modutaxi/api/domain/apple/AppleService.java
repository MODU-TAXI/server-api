package com.modutaxi.api.domain.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.domain.apple.AppleRequest.Events;
import com.modutaxi.api.domain.apple.AppleRequest.StsPayload;
import com.modutaxi.api.domain.apple.AppleRequest.StsRequest;
import com.modutaxi.api.domain.member.service.GetMemberService;
import com.modutaxi.api.domain.member.service.UpdateMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;


@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService {
    private final UpdateMemberService updateMemberService;
    private final GetMemberService getMemberService;

    public void appleServerToServer(StsPayload payload) {
        try {
            Events events = null;
            try {
                events = new ObjectMapper().readValue(decodePayload(payload.getPayload(), StsRequest.class).getEvents(), Events.class);
            } catch (JsonProcessingException e) {
                log.error("Apple Server To Server Error : Object Mapper Error");
            }
            if (events.getType().equals("consent-revoked") || events.getType().equals("account-delete")) {
                updateMemberService.deleteMember(getMemberService.getMemberByAppleSnsId(events.getSub()));
            }
        } catch (BaseException e) {
            log.error("Apple Server To Server Error : {}", e);
        }
    }

    private <T> T decodePayload(String token, Class<T> targetClass) {
        try {
            return (new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false))
                .readValue(new String(Base64.getDecoder().decode(token.split("\\.")[1])), targetClass);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}
