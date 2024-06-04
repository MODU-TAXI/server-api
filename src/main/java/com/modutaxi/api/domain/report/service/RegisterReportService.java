package com.modutaxi.api.domain.report.service;

import static com.modutaxi.api.common.constants.ServerConstants.REPORT_STANDARD;
import static com.modutaxi.api.domain.report.mapper.ReportMapper.toEntity;

import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.MemberErrorCode;
import com.modutaxi.api.common.exception.errorcode.ReportErrorCode;
import com.modutaxi.api.common.slack.SlackService;
import com.modutaxi.api.domain.mail.service.MailServiceImpl;
import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.repository.MemberRepository;
import com.modutaxi.api.domain.report.dto.ReportResponseDto.ReportResponse;
import com.modutaxi.api.domain.report.entity.Report;
import com.modutaxi.api.domain.report.entity.ReportType;
import com.modutaxi.api.domain.report.repository.ReportRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class RegisterReportService {

    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;
    private final SlackService slackService;
    private final MailServiceImpl mailService;

    public ReportResponse register(Long reporterId, Long targetId, ReportType type,
        String content) {
        // validation
        Member targetMember = memberRepository.findByIdAndStatusTrue(targetId)
            .orElseThrow(() -> new BaseException(MemberErrorCode.EMPTY_MEMBER));
        validateContentLength(content);

        // 같은 날, 같은 멤버가, 같은 멤버를 신고한게 아니라면
        if (!existsTodayDuplicateReport(reporterId, targetId)) {
            // 신고 횟수 카운팅
            targetMember.plusOneReportCount();
        }

        // 저장
        Report report = toEntity(reporterId, targetId, type, content);
        reportRepository.save(report);

        // 관리자 슬랙에 메시지 전송
        slackService.sendReportMessage(report);

        // 신고 누적 체크 및 대응
        checkExceededReportStandard(targetMember);

        return new ReportResponse(report.getId());
    }

    private void validateContentLength(String content) {
        if (content.length() < 10) {
            throw new BaseException(ReportErrorCode.SHORT_CONTENT);
        }
    }

    /**
     * 24시간 이내, 같은 멤버를 신고한 이력이 있는지 확인하여 boolean을 반환합니다.
     */
    private boolean existsTodayDuplicateReport(Long reporterId, Long targetId) {
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return reportRepository.existsByCreatedAtAfter24AndReportedIdAndTargetId(
            twentyFourHoursAgo, reporterId, targetId);

    }

    /**
     * 해당 멤버에 대한 신고가 기준치 이상 누적되었다면 임시 비활성화 처리 후, 관리자 슬랙에 메시지를 전송합니다.
     */
    private void checkExceededReportStandard(Member member) {
        if (member.getReportCount() >= REPORT_STANDARD && member.isStatus()) {
            // 임시 비활성화 처리
            member.setBlockedTrue();
            // 관리자 슬랙에 메시지 전송
            slackService.sendTemporaryBlockMemberMessage(member);
            // 이메일 존재하면 비활성화 처리 이메일 전송
            if (member.getEmail() != null) {
                mailService.sendTemporaryBlockMemberMail(member.getEmail());
            }
        }
    }

}
