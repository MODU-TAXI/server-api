package com.modutaxi.api.domain.report.service;

import static com.modutaxi.api.common.constants.ServerConstants.REPORT_STANDARD;
import static com.modutaxi.api.domain.report.mapper.ReportMapper.toEntity;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ReportErrorCode;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.report.dto.ReportResponseDto.ReportResponse;
import com.modutaxi.api.domain.report.entity.Report;
import com.modutaxi.api.domain.report.entity.ReportType;
import com.modutaxi.api.domain.report.repository.ReportRepository;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterReportService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    public ReportResponse register(Long reporterId, Long targetId, ReportType type,
        String content) {
        Member targetMember = memberRepository.findByIdAndStatusTrue(targetId)
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));

        validateContentLength(content);

        Report report = toEntity(reporterId, targetId, type, content);
        reportRepository.save(report);

        // 같은 날, 같은 멤버가, 같은 멤버를 신고 했다면 카운팅하지 않습니다.
        if (!existsTodayDuplicateReport(reporterId, targetId)) {
            targetMember.plusOneReportCount();
        }

        if (targetMember.getReportCount() >= REPORT_STANDARD) {
            // TODO: 신고가 기준 치 이상 누적되었을 때, 임시 비활성화 처리 및 슬랙 메시지 전송 로직 추가
            // TODO: 비활성화 처리 이메일 전송 필요하다면 로직 추가
        }

        return new ReportResponse(report.getId());
    }

    private void validateContentLength(String content) {
        if (content.length() < 10) {
            throw new BaseException(ReportErrorCode.SHORT_CONTENT);
        }
    }

    /**
     * 오늘, 같은 멤버를 신고한 이력이 있는지 확인하여 boolean을 반환합니다.
     */
    private boolean existsTodayDuplicateReport(Long reporterId, Long targetId) {
        String date = String.valueOf(LocalDate.now());
        return reportRepository.existsByCreatedAtAndReportedIdAndTargetId(
            date, reporterId, targetId);
    }
}
