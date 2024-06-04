package com.modutaxi.api.domain.report.dto;

import com.modutaxi.api.domain.report.entity.ReportType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ReportRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReportRequest {
        @Schema(example = "2", description = "신고 대상의 Id")
        private Long targetId;
        @Schema(example = "LATE", description = "신고 유형")
        private ReportType type;
        @Schema(example = "이 사람 제 시간 보다 너무 늦었어요..", description = "신고 내용")
        private String content;
    }
}
