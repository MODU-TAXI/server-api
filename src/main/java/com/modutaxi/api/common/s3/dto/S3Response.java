package com.modutaxi.api.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class S3Response {
    @Getter
    @AllArgsConstructor
    public static class S3UploadResponse {
        private String imageUrl;
        private String fileName;
    }

    @Getter
    @AllArgsConstructor
    public static class S3DeleteResponse {
        private Boolean isDeleted;
    }
}
