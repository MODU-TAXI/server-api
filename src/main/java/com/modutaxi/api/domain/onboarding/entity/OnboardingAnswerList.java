package com.modutaxi.api.domain.onboarding.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OnboardingAnswerList {

    ETC(0,0, "기타"),

    ANSWER1(1, 1,"에브리타임을 통해 알게 되었어요!"),
    ANSWER2(1, 2, "지인 추천을 통해 알게 되었어요!"),
    ANSWER3(1, 3, "직접 검색해서 알게 되었어요!"),

    ANSWER4(2, 1, "지각할 것 같을 때"),
    ANSWER5(2, 2, "버스 줄이 너무 길 때"),
    ANSWER6(2, 3, "편하게 가고 싶을 때")
    ;

    private final int questionId;
    private final int answerId;
    private final String content;
}
