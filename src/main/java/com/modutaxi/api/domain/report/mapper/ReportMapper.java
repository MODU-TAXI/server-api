package com.modutaxi.api.domain.report.mapper;

import com.modutaxi.api.domain.report.entity.Report;
import com.modutaxi.api.domain.report.entity.ReportType;
import org.springframework.stereotype.Component;

@Component
public class ReportMapper {

    public static Report toEntity(Long reporterId, Long targetId, Long roomId, ReportType type,
        String content) {
        return Report.builder()
            .reporterId(reporterId)
            .targetId(targetId)
            .roomId(roomId)
            .type(type)
            .content(content)
            .build();
    }
}
