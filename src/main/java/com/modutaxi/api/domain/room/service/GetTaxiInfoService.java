package com.modutaxi.api.domain.room.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GetTaxiInfoService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;


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
        this.objectMapper = new ObjectMapper();
    }

    public JsonNode getDrivingInfo() {

        // 출발지와 도착지 좌표
        String startCoord = "126.679554, 37.464164";
        String goalCoord = "126.656386, 37.451319";

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

        JsonNode jsonNode = extractRouteInfo(jsonResponse);

        int code = jsonNode.get("code").asInt();

        if (code == 1) {
            throw new BaseException(TaxiInfoErrorCode.SAME_ORIGIN_DESTINATION);
        } else if (code == 2) {
            throw new BaseException(TaxiInfoErrorCode.NOT_AROUND_ROAD);
        } else if (code == 3) {
            throw new BaseException(TaxiInfoErrorCode.FIND_ROUTE_FAIL);
        } else if (code == 4) {
            throw new BaseException(TaxiInfoErrorCode.STOPOVER_NOT_AROUND_ROAD);
        } else if (code == 5) {
            throw new BaseException(TaxiInfoErrorCode.TOO_LONG_PATH);
        }

        return jsonNode;
    }


    public JsonNode extractRouteInfo(String jsonResponse) {

        JsonNode jsonNode;

        try {
            jsonNode = objectMapper.readTree(jsonResponse);
        } catch (JsonProcessingException e) {
            throw new BaseException(TaxiInfoErrorCode.JSON_PROCESSING_FAIL);
        }

        ObjectNode extractedInfo = objectMapper.createObjectNode();

        int code = jsonNode.get("code").asInt();
        extractedInfo.put("code", code);

        //diriving API에 정의된 예외처리
        if (code == 0) {

            //예상 도착시간 추가
            long duration = jsonNode.get("route")
                .get("traoptimal")
                .get(0)
                .get("summary")
                .get("duration")
                .asLong();
            extractedInfo.put("duration", duration);

            // 예상 택시요금 추가
            double taxiFare = jsonNode.get("route")
                .get("traoptimal")
                .get(0)
                .get("summary")
                .get("taxiFare")
                .asDouble();
            extractedInfo.put("taxiFare", taxiFare);

            // 시작 주소 추가
            JsonNode startLocation = jsonNode.get("route")
                .get("traoptimal")
                .get(0)
                .get("summary")
                .get("start")
                .get("location");
            extractedInfo.set("startLocation", startLocation);

            // 경로 정보 추가
            JsonNode pathArray = jsonNode.get("route")
                .get("traoptimal")
                .get(0)
                .get("path");
            extractedInfo.set("path", pathArray);
        }

        return extractedInfo;
    }
}
