package com.modutaxi.api.domain.onboarding.mapper;

import com.modutaxi.api.domain.onboarding.entity.Onboarding;
import com.modutaxi.api.domain.onboarding.entity.OnboardingEtc;
import org.springframework.stereotype.Component;

@Component
public class OnboardingMapper {

    public Onboarding toEntity(int questionId) {
        return Onboarding.builder()
                .questionId(questionId)
                .build();
    }

    public OnboardingEtc toEntity(int questionId, String content) {
        return OnboardingEtc.builder()
                .questionId(questionId)
                .content(content)
                .build();
    }
}
