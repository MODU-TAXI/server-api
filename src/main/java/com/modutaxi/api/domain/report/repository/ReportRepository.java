package com.modutaxi.api.domain.report.repository;

import com.modutaxi.api.domain.report.entity.Report;
import io.lettuce.core.dynamic.annotation.Param;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Long> {

    /**
     * 오늘 날짜에 reporte
     */
    @Query(value = "SELECT CASE WHEN COUNT(r) > 0 THEN TRUE ELSE FALSE END "
        + "FROM Report r "
        + "WHERE date_format(r.createdAt, '%Y-%m-%d') = :date "
        + "AND r.reporterId = :reporterId "
        + "AND r.targetId = :targetId")
    boolean existsByCreatedAtAndReportedIdAndTargetId(
        @Param("date") String date,
        @Param("reporterId") Long reporterId,
        @Param("targetId") Long targetId);

    boolean existsByRoomIdAndReporterIdAndTargetId(
        @Param("roomId") Long roomId,
        @Param("reporterId") Long reporterId,
        @Param("targetId") Long targetId);
}
