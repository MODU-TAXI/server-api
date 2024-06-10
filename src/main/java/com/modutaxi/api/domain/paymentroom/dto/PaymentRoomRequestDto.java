package com.modutaxi.api.domain.paymentroom.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PaymentRoomRequestDto {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentRoomRequest {
        private Long roomId;                    // 방 ID
        private Long accountId;                 // 계좌 ID
        private int totalCharge;                // 총 금액
        private List<Long> ParticipantList;     // 탄 사람
        private List<Long> nonParticipantList;  // 안 탄 사람
    }
}
