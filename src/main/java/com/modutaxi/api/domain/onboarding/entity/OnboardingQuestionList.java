package com.modutaxi.api.domain.onboarding.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OnboardingQuestionList {

    QUESTION1(1, "모두의 택시, 어떻게 이용하게 되셨나요?", 3),
    QUESTION2(2, "택시를 가장 타고 싶었던 순간이 있으신가요?", 3),
    ;

    private final int questionId;
    private final String content;
    private final int numberOfAnswer;
}
