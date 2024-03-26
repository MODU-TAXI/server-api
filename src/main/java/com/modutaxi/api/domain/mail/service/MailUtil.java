package com.modutaxi.api.domain.mail.service;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailResult;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailUtil {
    private final AmazonSimpleEmailService amazonSimpleEmailService;

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
