package com.modutaxi.api.domain.onboarding.service;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.OnboardingErrorCode;
import com.modutaxi.api.domain.onboarding.dto.OnboardingRequestDto.OnboardingRequest;
import com.modutaxi.api.domain.onboarding.entity.Onboarding;
import com.modutaxi.api.domain.onboarding.mapper.OnboardingMapper;
import com.modutaxi.api.domain.onboarding.repository.OnboardingEtcRepository;
import com.modutaxi.api.domain.onboarding.repository.OnboardingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class RegisterOnboardingService {

    private final OnboardingRepository onboardingRepository;
    private final OnboardingEtcRepository onboardingEtcRepository;
    private final OnboardingMapper onboardingMapper;

    public void registerOnboarding(OnboardingRequest onboardingRequest) {
        Onboarding onboarding = onboardingRepository.findByQuestionId(onboardingRequest.getQuestionId())
                .orElseThrow(() -> new BaseException(OnboardingErrorCode.EMPTY_ONBOARDING));

        // 대답이 true 면 카운트 ++, 확장 시 여기 부분만 수정하면 됩니다!
        onboarding.upAnswerCount(
                onboardingRequest.isEtc(),
                onboardingRequest.isAnswer1(),
                onboardingRequest.isAnswer2(),
                onboardingRequest.isAnswer3()
        );

        // 응답자 수 ++
        onboarding.upRespondentCount();

        // 기타에 응답했다면 OnboardingEtc register
        if(onboardingRequest.isEtc()) {
            registerOnboardingEtc(onboarding.getQuestionId(), onboardingRequest.getEtcContent());
        }
    }

    public void registerOnboardingEtc(int questionId, String content) {
        onboardingEtcRepository.save(
                onboardingMapper.toEntity(questionId, content)
        );
    }
}