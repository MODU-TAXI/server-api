package com.modutaxi.api.domain.mail.service;

import static com.modutaxi.api.common.exception.errorcode.MailErrorCode.SES_SERVER_ERROR;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.util.cert.CertificationCodeUtil;
import com.modutaxi.api.domain.mail.vo.MailTemplate;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {
    private final AmazonSimpleEmailService amazonSimpleEmailService;
    @Value("${mail.sender-address.no-reply}")
    private String noReplySender;
    @Value("${mail.receiver-address.banker-list}")
    private List<String> bankerEmailList;

    public static Boolean emailAddressFormVerification(String emailAddress) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        return emailAddress.matches(emailRegex);
    }

    public String sendEmailCertificationHtmlMail(String receiver) {
        String certificationCode = CertificationCodeUtil.generateCertificationCode(5);
        sendSimpleEmailOnlyHtml(
            "[모두의 택시] 인증코드를 안내해드립니다."
            , noReplySender
            , MailTemplate.getCertMailContent(receiver, certificationCode)
            , receiver);
        return certificationCode;
    }

    public void sendEmailCoolSmsBalanceMail(Long balance) {
        for (String bankerEmail : bankerEmailList) {
            sendSimpleEmailOnlyHtml(
                    "[모두의 택시] Cool SMS 잔액 부족"
                    , noReplySender
                    , String.format("Cool SMS의 잔액이 %s원 남았습니다. 요금을 충전하세요.", balance)
                    , bankerEmail
            );
        }
    }

    public void sendAligoRemainSmsMail(String smsCnt) {
        for (String bankerEmail : bankerEmailList) {
            sendSimpleEmailOnlyHtml(
                "[모두의 택시] Aligo 잔액 부족"
                , noReplySender
                , String.format("Aligo의 남은 SMS 개수가 %s개 입니다. 요금을 충전하세요.", smsCnt)
                , bankerEmail
            );
        }
    }

    public void sendBlockMemberNotificationMail(String receiver) {
        sendSimpleEmailOnlyHtml(
            "[모두의 택시] 신고 누적으로 인해 귀하의 계정이 임시 차단되었습니다."
            , noReplySender
            , MailTemplate.getTemporaryBlockMemberMailContent(receiver)
            , receiver);
    }

    private SendEmailResult sendSimpleEmailOnlyHtml(String title, String sender, String htmlContent,
        String receiver) {
        Message message = new Message();
        message.setSubject(new Content(title));
        message.setBody(new Body().withHtml(new Content(htmlContent)));

        SendEmailRequest request = new SendEmailRequest(sender, new Destination().withToAddresses(receiver), message);
        SendEmailResult result = amazonSimpleEmailService.sendEmail(request);
        if (result.getSdkHttpMetadata().getHttpStatusCode() != 200) {
            throw new BaseException(SES_SERVER_ERROR, "메일 발송에 실패했습니다.\n" + result.getSdkHttpMetadata().toString());
        }
        return result;
    }

    // full content mail
    private SendRawEmailResult sendRawEmail(String title, String sender, String content, String receiver, String html, String fileRoot) throws MessagingException, IOException, NullPointerException {
        Session session = Session.getDefaultInstance(new Properties());
        MimeMessage message = new MimeMessage(session);

        // 메일 제목 설정
        message.setSubject(title);

        // 메일 발신자 설정
        message.setFrom(sender);

        // 메일 수신자 설정
        message.setRecipients(jakarta.mail.Message.RecipientType.TO, InternetAddress.parse(receiver));
        MimeMultipart messageBody = new MimeMultipart("alternative");

        // HTML, text wrapper 설정
        MimeBodyPart wrap = new MimeBodyPart();

        // text
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(content, "text/plain; charset=UTF-8");
        messageBody.addBodyPart(textPart);

        // html
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");
        messageBody.addBodyPart(htmlPart);

        // wrapper에 content 추가
        wrap.setContent(messageBody);

        // 파일 적재를 위한 MimeMultipart 설정
        MimeMultipart msg = new MimeMultipart("mixed");
        message.setContent(msg);
        msg.addBodyPart(wrap);

        MimeBodyPart att = new MimeBodyPart();
        DataSource fds = new FileDataSource(fileRoot);
        att.setDataHandler(new DataHandler(fds));
        att.setFileName(fds.getName());

        msg.addBodyPart(att);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);

        // outputStream을 RawMessage로 변환
        RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

        SendRawEmailResult result = amazonSimpleEmailService.sendRawEmail(new SendRawEmailRequest(rawMessage));
        return result;
    }
}
