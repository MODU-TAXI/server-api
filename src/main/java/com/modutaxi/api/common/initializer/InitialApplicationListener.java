package com.modutaxi.api.common.initializer;

import com.modutaxi.api.domain.onboarding.mapper.OnboardingMapper;
import com.modutaxi.api.domain.onboarding.repository.OnboardingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitialApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {

    private final OnboardingRepository onboardingRepository;
    private final OnboardingMapper onboardingMapper;

    private static final int NUMBER_OF_QUESTION = 2;

    /**
     * 서버 실행 시 DB에 질문이 존재하지 않는다면 먼저 register 해준다.
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        for(int i = 1; i <= NUMBER_OF_QUESTION; i++) {
            if(onboardingRepository.findByQuestionId(i).isEmpty()) {
                onboardingRepository.save(onboardingMapper.toEntity(i));
                log.info("InitializingOnboarding: {}", i);
            }
        }
    }

}

