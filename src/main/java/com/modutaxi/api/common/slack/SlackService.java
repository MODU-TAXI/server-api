package com.modutaxi.api.common.slack;

import static com.slack.api.webhook.WebhookPayloads.payload;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.member.service.GetMemberService;
import com.modutaxi.api.domain.report.entity.Report;
import com.modutaxi.api.domain.room.service.GetRoomService;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Log4j2
@RequiredArgsConstructor
public class SlackService {

    private final Slack slackClient = Slack.getInstance();
    private final GetMemberService getMemberService;
    private final GetRoomService getRoomService;

    @Value("${slack.webhook-uri.report}")
    private String reportSlackToken;

    @Value("${slack.webhook-uri.member}")
    private String memberSlackToken;

    @Value("${slack.webhook-uri.stats}")
    private String statsSlackToken;


    /**
     * 슬랙 메시지 전송
     *
     * @Param token 전송할 채널의 웹훅 토큰
     * @Param title 메시지의 제목
     * @Param 메시지 데이터 셋
     * @Param 메시지 컬러 코드
     **/
    public void sendMessage(String token, String title, LinkedHashMap<String, String> data,
        String colorCode) {
        try {
            slackClient.send(token, payload(p -> p
                .text(title) // 메시지 제목
                .attachments(List.of(
                    Attachment.builder().color(colorCode) // 메시지 색상
                        .fields( // 메시지 본문 내용
                            data.keySet().stream()
                                .map(key -> generateSlackField(key, data.get(key))).collect(
                                    Collectors.toList())
                        ).build())))
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 신고 발생 시 슬랙 메시지 전송
     **/
    public void sendReportMessage(Report report) {
        String title = "[신고 접수]";
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("택시팟 ID", Long.toString(report.getRoomId()));
        data.put("신고자 ID", Long.toString(report.getReporterId()));
        data.put("신고 대상자 ID", Long.toString(report.getTargetId()));
        data.put("신고 유형", report.getType().getMessage());
        data.put("신고 내용", report.getContent());

        sendMessage(reportSlackToken, title, data, Color.GREEN.getCode());
    }

    /**
     * 신고 누적으로 인한 임시 차단 멤버 발생 시 슬랙 메시지 전송
     **/
    public void sendTemporaryBlockMemberMessage(Member member) {
        String title = "[신고 누적으로 인한 멤버 임시 차단]";
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("멤버 ID", Long.toString(member.getId()));
        data.put("닉네임", member.getNickname());

        sendMessage(reportSlackToken, title, data, Color.RED.getCode());
    }

    /**
     * 신규 회원가입 시 슬랙 메시지 전송
     **/
    public void sendNewMemberMessage(Member member) {
        String title = "[신규 회원가입]";
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("멤버 ID", Long.toString(member.getId()));
        data.put("이름", member.getName());
        data.put("전화번호", member.getPhoneNumber());

        sendMessage(memberSlackToken, title, data, Color.GREEN.getCode());
    }

    /**
     * 회원 탈퇴 시 슬랙 메시지 전송
     **/
    public void sendDeleteMemberMessage(Member member) {
        String title = "[회원 탈퇴]";
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("멤버 ID", Long.toString(member.getId()));
        data.put("이름", member.getName());
        data.put("전화번호", member.getPhoneNumber());

        sendMessage(memberSlackToken, title, data, Color.RED.getCode());
    }

    /**
     * 매일 23시 55분 00초에 가입, 이용통계 슬랙 메시지 전송
     **/
    @Scheduled(cron = "00 55 23 * * *")
    public void sendStatsMessage() {
        log.info("[SlackService] 가입 및 이용 통계 Scheduler 작동");
        String title = "[데일리 통계]";
        LinkedHashMap<String, String> data = new LinkedHashMap<>();
        data.put("날짜", LocalDate.now().format(DateTimeFormatter.ofPattern("YYYY년 MM월 dd일")));
        data.put("가입자", getMemberService.countTodaySignups() + " 명");
        data.put("생성된 택시팟", getRoomService.countTodayRoom(0, 3) + " 개");
        data.put("매칭 완료된 택시팟", getRoomService.countTodayRoom(1, 3) + " 개");
        data.put("정산 완료된 택시팟", getRoomService.countTodayRoom(3, 3) + " 개");
        data.put("삭제된 택시팟", getRoomService.countTodayRoom(4, 4) + " 개");

        sendMessage(statsSlackToken, title, data, Color.YELLOW.getCode());
    }


    /**
     * Slack Field 생성
     **/
    private Field generateSlackField(String title, String value) {
        return Field.builder()
            .title(title)
            .value(value)
            .valueShortEnough(false)
            .build();
    }
}
