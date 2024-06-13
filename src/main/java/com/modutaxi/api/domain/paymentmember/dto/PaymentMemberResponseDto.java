package com.modutaxi.api.domain.paymentmember.dto;

import com.modutaxi.api.domain.paymentmember.entity.PaymentMemberStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PaymentMemberResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaymentMemberResponse {
        @Schema(example = "3")
        private Long id;
        @Schema(example = "보라보라")
        private String nickName;
        @Schema(example = "(유*수)")
        private String name;
        @Schema(example = "https://...")
        private String imageUrl;
        @Schema(example = "INCOMPLETE")
        private PaymentMemberStatus status;
        @Schema(example = "true")
        private boolean isMe;
    }

    @Getter
    @AllArgsConstructor
    public static class PaymentMemberListResponse {
        private List<PaymentMemberResponse> participantList;
    }

    @Getter
    @AllArgsConstructor
    public static class UpdatePaymentMemberResponse {
        @Schema(example = "true", description = "수행완료 여부")
        private boolean isUpdated;
    }
}
