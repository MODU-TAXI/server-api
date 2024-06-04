package com.modutaxi.api.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class ReportResponseDto {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ReportResponse {
        @Schema(example = "1", description = "신고 Id")
        private Long reportId;
    }
}
