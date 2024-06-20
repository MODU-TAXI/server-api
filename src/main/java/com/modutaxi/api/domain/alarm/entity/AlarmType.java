package com.modutaxi.api.domain.alarm.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AlarmType {

    // 매칭
    PARTICIPATE_REQUEST("새로운 멤버가 매칭 대기중이에요!"),
    MATCHING_SUCCESS("매칭이 수락되었어요! 지금 바로 채팅을 시작하세요."),
    MATCHING_COMPLETE("모든 인원이 모였어요. 매칭 완료를 눌러주세요."),

    // 신고
    REPORT_SUCCESS("신고가 접수되었어요, 빨리 해결해드릴게요!"),

    // 정산
    PAYMENT_REQUEST("정산 금액을 입력해주세요."),
    PAYMENT_REQUEST_COMPLETE("정산 요청이 들어왔어요. 금액을 확인해주세요."),
    PAYMENT_ALL_COMPLETE("정산이 완료되었어요!");

    private final String message;

}
