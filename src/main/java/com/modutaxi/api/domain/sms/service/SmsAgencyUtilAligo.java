package com.modutaxi.api.domain.sms.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.SmsErrorCode;
import com.modutaxi.api.domain.mail.service.MailService;
import com.modutaxi.api.domain.sms.dto.AligoResponseDto.AligoErrorResponse;
import com.modutaxi.api.domain.sms.dto.AligoResponseDto.CheckBalanceResponse;
import com.modutaxi.api.domain.sms.dto.AligoResponseDto.GetPrevMessageResponseList;
import com.modutaxi.api.domain.sms.dto.AligoResponseDto.SendSmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Primary
public class SmsAgencyUtilAligo implements SmsAgencyUtil {
    private final MailService mailService;
    @Value("${api.aligo.api-key}")
    private String apiKey;
    @Value("${api.aligo.user-id}")
    private String userId;
    @Value("${api.aligo.checkRemainSmsThreshold}")
    private Integer checkRemainSmsThreshold;

    @Override
    public void getPrevMessage(String sender, String phoneNumber, String messageId) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("mid", messageId));
        GetPrevMessageResponseList response = aligoReqeust("https://apis.aligo.in/sms_list/", nvps, GetPrevMessageResponseList.class);
        if (response.getList().isEmpty())
            return;
        switch (response.getList().get(0).getSms_state()) {
            case ("발송완료") -> {
                return;
            }
            case ("전송중") -> throw new BaseException(SmsErrorCode.CERTIFICATION_CODE_SENDING);
            default -> log.error("[Aligo] 같은 조건의 직전의 문자 전송에 실패했습니다. " + response.getList().get(0).getSms_state());
        }
    }

    @Override
    public String sendOne(String sender, String phoneNumber, String text) {
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("sender", sender));
        nvps.add(new BasicNameValuePair("receiver", phoneNumber));
        nvps.add(new BasicNameValuePair("msg", text));
        nvps.add(new BasicNameValuePair("msg_type", "SMS"));
        SendSmsResponse response = aligoReqeust("https://apis.aligo.in/send/", nvps, SendSmsResponse.class);
        return Long.toString(response.getMsg_id());
    }

    @Override
    public void checkBalance() {
        List<NameValuePair> nvps = new ArrayList<>();
        CheckBalanceResponse response = aligoReqeust("https://apis.aligo.in/remain/", nvps, CheckBalanceResponse.class);
        if (response.getSMS_CNT() < checkRemainSmsThreshold) {
            mailService.sendAligoRemainSmsMessage(Long.toString(response.getSMS_CNT()));
            log.warn(String.format("[Aligo] 남은 가능한 SMS 전송 개수는 %s개 입니다.", response.getSMS_CNT()));
        }
    }

    private <T> T aligoReqeust(String url, List<NameValuePair> nvps, Class<T> responseType) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = generateAligoHttpPost(httpClient, url, nvps);
        CloseableHttpResponse response = executeAligoHttpClient(httpClient, httpPost);
        AligoErrorHandle(httpClient, response);
        T responseBody = mapValue(httpClient, response, responseType);
        closeHttpClient(httpClient);
        return responseBody;
    }

    private HttpPost generateAligoHttpPost(CloseableHttpClient httpClient, String url, List<NameValuePair> nvps) {
        HttpPost httpPost = new HttpPost(url);
        nvps.add(new BasicNameValuePair("key", apiKey));
        nvps.add(new BasicNameValuePair("user_id", userId));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("[Aligo] 문자 전송 메시지 인코딩에 실패했습니다.");
            closeHttpClient(httpClient);
            throw new BaseException(SmsErrorCode.SMS_AGENCY_ERROR);
        }
        return httpPost;
    }

    private CloseableHttpResponse executeAligoHttpClient(CloseableHttpClient httpClient, HttpPost httpPost) {
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            log.error("[Aligo] 메세지 서버와 연결에 실패했습니다.");
            closeHttpClient(httpClient);
            throw new BaseException(SmsErrorCode.SMS_AGENCY_ERROR);
        }
        return response;
    }

    private void AligoErrorHandle(CloseableHttpClient httpClient, CloseableHttpResponse response) {
        if (response.getStatusLine().getStatusCode() > 0) return;

        AligoErrorResponse errorBody = mapValue(httpClient, response, AligoErrorResponse.class);
        switch (response.getStatusLine().getStatusCode()) {
            case (-101) -> log.error("[Aligo] 인증오류입니다.");
            case (-304) -> log.error("[Aligo] 발송 5분전까지만 취소가 가능합니다.");
            default ->
                log.error(String.format("[Aligo] 알 수 없는 에러가 발생했습니다. (%s, %s)", errorBody.getResult_code(), errorBody.getMessage()));
        }
        closeHttpClient(httpClient);
        throw new BaseException(SmsErrorCode.SMS_AGENCY_ERROR);
    }

    private <T> T mapValue(CloseableHttpClient httpClient, CloseableHttpResponse response, Class<T> bodyType) {
        ObjectMapper objectMapper = new ObjectMapper()
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        T responseBody;
        try {
            responseBody = objectMapper.readValue(EntityUtils.toString(response.getEntity(), "UTF-8"), bodyType);
        } catch (IOException e) {
            log.error(String.format("[Aligo] 문자 전송 응답 메시지(%s dto) 파싱에 실패했습니다.", bodyType.getName()));
            closeHttpClient(httpClient);
            throw new BaseException(SmsErrorCode.SMS_AGENCY_ERROR);
        }
        return responseBody;
    }

    private void closeHttpClient(CloseableHttpClient httpClient) {
        try {
            httpClient.close();
        } catch (IOException e) {
            log.error("[Aligo] HTTP 클라이언트 종료에 실패했습니다.");
        }
    }
}
