package com.modutaxi.api.domain.report.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportType {

    LEAVE_CHATROOM("중간에 채팅방을 나갔어요"),
    LATE("제 시간에 도착하지 않았어요"),
    FIRST_GONE("먼저 출발했어요"),
    OUT_OF_TOUCH("연락이 되지 않아요"),
    UNEXPECTED_ACCOUNTS("정산 금액이 예상과 달라요"),
    NOT_REMIT("정산 금액을 보내주지 않았어요"),
    ETC("기타 (직접 입력할게요)");

    public final String message;
}
