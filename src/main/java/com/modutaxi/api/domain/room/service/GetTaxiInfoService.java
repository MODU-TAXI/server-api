package com.modutaxi.api.domain.room.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GetTaxiInfoService {

    private final WebClient webClient;
    private final Gson gson;

    @Value("${api.naver.client-id}")
    private String clientId;

    @Value("${api.naver.client-secret}")
    private String clientSecret;

    public GetTaxiInfoService() {
        this.webClient = WebClient.builder()
            .baseUrl("https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving")
            .defaultHeader("X-NCP-APIGW-API-KEY-ID", clientId)
            .defaultHeader("X-NCP-APIGW-API-KEY", clientSecret)
            .build();
        this.gson = new Gson();
    }

    public JsonObject getDrivingInfo() {

        // 출발지와 도착지 좌표
        String startCoord = "126.679554,37.464164";
//        String goalCoord = "126.656386,37.451319";
        String goalCoord = "126.679554,37.464164";
        // 파라미터 설정

        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
            .queryParam("start", startCoord)
            .queryParam("goal", goalCoord);

        String jsonResponse = webClient.get()
            .uri(builder.toUriString())
            .header("X-NCP-APIGW-API-KEY-ID", clientId)
            .header("X-NCP-APIGW-API-KEY", clientSecret)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        JsonObject jsonObject = extractRouteInfo(gson.fromJson(jsonResponse, JsonObject.class));

        int code = jsonObject.get("code").getAsInt();

        if (code == 1) {
            throw new BaseException(TaxiInfoErrorCode.SAME_ORIGIN_DESTINATION);
        } else if (code == 2) {
            throw new BaseException(TaxiInfoErrorCode.NOT_AROUND_ROAD);
        } else if (code == 3) {
            throw new BaseException(TaxiInfoErrorCode.FAIL_FIND_ROUTE);
        } else if (code == 4) {
            throw new BaseException(TaxiInfoErrorCode.STOPOVER_NOT_AROUND_ROAD);
        } else if (code == 5) {
            throw new BaseException(TaxiInfoErrorCode.TOO_LONG_PATH);
        }

        return jsonObject;
    }

    public JsonObject extractRouteInfo(JsonObject jsonObject) {

        JsonObject extractedInfo = new JsonObject();

        int code = jsonObject.get("code").getAsInt();
        extractedInfo.addProperty("code", code);

        //diriving API에 정의된 예외처리
        if (code == 0) {
            //예상 도착시간 추가
            long duration = jsonObject.getAsJsonObject("route")
                .getAsJsonArray("traoptimal")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("summary")
                .getAsJsonPrimitive("duration")
                .getAsLong();
            extractedInfo.addProperty("duration", duration);

            // 예상 택시요금 추가
            double taxiFare = jsonObject.getAsJsonObject("route")
                .getAsJsonArray("traoptimal")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("summary")
                .getAsJsonPrimitive("taxiFare")
                .getAsDouble();
            extractedInfo.addProperty("taxiFare", taxiFare);

            // 시작 주소 추가
            JsonArray startLocation = jsonObject.getAsJsonObject("route")
                .getAsJsonArray("traoptimal")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("summary")
                .getAsJsonObject("start")
                .getAsJsonArray("location");
            extractedInfo.add("startLocation", startLocation);

            // 경로 정보 추가
            JsonArray pathArray = jsonObject.getAsJsonObject("route")
                .getAsJsonArray("traoptimal")
                .get(0)
                .getAsJsonObject()
                .getAsJsonArray("path");
            extractedInfo.add("path", pathArray);
        }

        return extractedInfo;
    }
}
