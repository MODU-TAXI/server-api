package com.modutaxi.api.common.s3.dto;

import lombok.Getter;

@Getter
public class S3Result {

    private String imgUrl;
    private String fileName;

    public S3Result(String imgUrl, String fileName) {
        this.imgUrl = imgUrl;
        this.fileName = fileName;
    }
}