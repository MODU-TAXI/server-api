package com.modutaxi.api.domain.sms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class AligoResponseDto {

    @Getter
    @NoArgsConstructor
    public static class AligoErrorResponse {
        private Long result_code;
        private String message;
    }

    @Getter
    @NoArgsConstructor
    public static class GetPrevMessageResponse {
        private Long mdid;
        private String type;
        private String sender;
        private String receiver;
        private String sms_state;
    }

    @Getter
    @NoArgsConstructor
    public static class GetPrevMessageResponseList {
        private Long result_code;
        private String message;
        private List<GetPrevMessageResponse> list;
    }

    @Getter
    @NoArgsConstructor
    public static class SendSmsResponse {
        private Long result_code;
        private String message;
        private Long msg_id;
        private Long success_cnt;
        private Long error_cnt;
        private String msg_type;
    }

    @Getter
    @NoArgsConstructor
    public static class CheckBalanceResponse {
        private Long result_code;
        private String message;
        @JsonProperty("SMS_CNT")
        private Long SMS_CNT;
        @JsonProperty("LMS_CNT")
        private Long LMS_CNT;
        @JsonProperty("MMS_CNT")
        private Long MMS_CNT;
    }
}
