package com.modutaxi.api.domain.chatmessage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MessageType {
    JOIN("채팅방 입장 시", null),
    CHAT("일반 메시지", null),
    LEAVE("채팅방 퇴장 시", null),
    IMAGE("이미지 전송", null),
    CHAT_BOT("챗봇", "모두의 택시 봇"),
    CALL_TAXI("택시 부를 때", "모두의 택시 봇"),
    MATCHING_COMPLETE("[방장에게 전송] 매칭 완료", "모두의 택시 봇"),
    PAYMENT_REQUEST("[방장에게 전송] 정산 요청", "모두의 택시 봇"),
    PAYMENT_REQUEST_COMPLETE("[방장이 모두에게 전송] 정산 요청", null),
    PAYMENT_COMPLETE("[멤버가 돈 보낸 후 모두에게 전송] 정산 완료", null);

    private final String description;
    private final String senderName;
}