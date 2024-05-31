package com.modutaxi.api.common.slack;

import static com.slack.api.webhook.WebhookPayloads.payload;

import com.modutaxi.api.domain.member.entity.Member;
import com.modutaxi.api.domain.report.entity.Report;
import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SlackService {

    private final Slack slackClient = Slack.getInstance();

    @Value("${slack.webhook-uri.report}")
    private String reportSlackToken;

    /**
     * 슬랙 메시지 전송
     *
     * @Param token 전송할 채널의 웹훅 토큰
     * @Param title 메시지의 제목
     * @Param 메시지 데이터 셋
     * @Param 메시지 컬러 코드
     **/
    public void sendMessage(String token, String title, HashMap<String, String> data,
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
        String title = "새로운 신고가 접수되었습니다!";
        HashMap<String, String> data = new HashMap<>();
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
        String title = "신고 누적으로 인해 임시 차단 멤버가 발생했습니다!";
        HashMap<String, String> data = new HashMap<>();
        data.put("멤버 ID", Long.toString(member.getId()));
        data.put("닉네임", member.getNickname());

        sendMessage(reportSlackToken, title, data, Color.RED.getCode());
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
