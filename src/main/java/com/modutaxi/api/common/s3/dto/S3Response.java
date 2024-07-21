package com.modutaxi.api.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

public class S3Response {
    @Getter
    @AllArgsConstructor
    @ToString
    public static class S3UploadResponse {
        private String imageUrl;
        private String fileName;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class S3DeleteResponse {
        private Boolean isDeleted;
    }
}
