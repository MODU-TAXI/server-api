package com.modutaxi.api.common.auth.oauth;

import com.google.gson.JsonParser;
import com.modutaxi.api.common.auth.oauth.apple.service.AppleService;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.AuthErrorCode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.net.ssl.HttpsURLConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final AppleService appleService;

    @Value("${kakao.admin-key}")
    private String adminKey;

    public String getKaKaoSnsId(String accessToken) throws IOException {
        String requestUrl = "https://kapi.kakao.com/v2/user/me";
        StringBuilder result = getKaKaoLoginResponse(accessToken, requestUrl);
        return JsonParser.parseString(result.toString()).
            getAsJsonObject().get("id").getAsString();
    }

    public void unlinkKakao(String snsId) throws IOException {
        if (!checkSocialTypeIsKakao(snsId)) {
            return;
        }
        String requestUrl = "https://kapi.kakao.com/v1/user/unlink";
        StringBuilder result = getKaKaoUnlinkResponse(snsId, requestUrl);
        log.info("id: {}",
            JsonParser.parseString(result.toString()).getAsJsonObject().get("id").getAsString());
    }

    public StringBuilder getKaKaoLoginResponse(String accessToken, String requestUrl)
        throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(HttpMethod.GET.name());
        conn.setRequestProperty("Authorization", " Bearer " + accessToken);

        if (conn.getResponseCode() >= 400) {
            throw new BaseException(AuthErrorCode.INVALID_ACCESS_TOKEN);
        }
        return getResponse(conn);
    }

    public StringBuilder getKaKaoUnlinkResponse(String snsId, String requestUrl)
        throws IOException {
        URL url = new URL(requestUrl);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        try {
            conn.setRequestMethod(HttpMethod.POST.name());
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Authorization", " KakaoAK " + adminKey);
            conn.setDoOutput(true);

            String body = "target_id_type=user_id";
            body += "target_id=" + snsId;

            OutputStream os = conn.getOutputStream();
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
            os.close();

        } catch (Exception e) {
            log.error("Unlink Kakao: Connection Setting Error");
            log.error("Error: {}", e);
        }
        if (conn.getResponseCode() >= 400) {
            log.error("Response Code: {}", conn.getResponseCode());
            throw new BaseException(AuthErrorCode.KAKAO_REVOKE_ERROR);
        }
        return getResponse(conn);
    }

    public String getAppleSub(String authorizationCode) {
        return appleService.getAppleIdTokenResponse(authorizationCode).getSub();
    }

    private StringBuilder getResponse(HttpsURLConnection conn) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = br.readLine()) != null) {
            result.append(line);
        }
        br.close();
        return result;
    }

    private boolean checkSocialTypeIsKakao(String snsId) {
        return snsId.length() == 10;
    }
}
