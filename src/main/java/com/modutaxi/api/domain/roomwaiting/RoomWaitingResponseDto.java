package com.modutaxi.api.domain.roomwaiting;

import com.modutaxi.api.domain.chatmessage.dto.ChatMessageResponseDto;
import com.modutaxi.api.domain.chatmessage.entity.ChatMessage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class RoomWaitingResponseDto {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class RoomWaitingResponse {
        private Long memberId;

        public static RoomWaitingResponse toDto(String memberId) {
            return RoomWaitingResponse.builder()
                    .memberId(Long.valueOf(memberId))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    @Builder
    public static class MemberRoomInResponse {
        private Long memberId;

        public static MemberRoomInResponse toDto(String memberId) {
            return MemberRoomInResponse.builder()
                    .memberId(Long.valueOf(memberId))
                    .build();
        }
    }
}
