package com.modutaxi.api.domain.taxiinfo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.TaxiInfoErrorCode;
import jakarta.transaction.Transactional;
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

    @Transactional
    public JsonNode getDrivingInfo(String startCoordinate, String goalCoordinate) {

        // 파라미터 설정
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
            .queryParam("start", startCoordinate)
            .queryParam("goal", goalCoordinate);

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

            JsonNode tempNode = jsonNode.get("route")
                .get("traoptimal")
                .get(0);

            //예상 도착시간 추가
            long duration = tempNode.get("summary")
                .get("duration")
                .asLong();
            extractedInfo.put("duration", duration);

            // 예상 택시요금 추가
            int taxiFare = tempNode.get("summary")
                .get("taxiFare")
                .asInt();
            extractedInfo.put("taxiFare", taxiFare);

            // 시작 주소 추가
            JsonNode startLocation = tempNode.get("summary")
                .get("start")
                .get("location");
            extractedInfo.set("startLocation", startLocation);

            // 경로 정보 추가
            JsonNode pathArray = tempNode.get("path");
            extractedInfo.set("path", pathArray);

            //끝 주소 추가
            JsonNode goalLocation = tempNode.get("summary")
                .get("goal")
                .get("location");
            extractedInfo.set("endLocation", goalLocation);
        }

        return extractedInfo;
    }

}
