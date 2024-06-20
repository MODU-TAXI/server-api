package com.modutaxi.api.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public final class ServerConstants {

    @Value("${cloud.aws.s3.basic-profile-image}")
    private String basicProfileImageUrl;

    public static String BASIC_PROFILE_IMAGE_URL;
    public static final int REPORT_STANDARD = 5;
    public static final int FULL_MEMBER = 4;

    @PostConstruct
    private void init() {
        BASIC_PROFILE_IMAGE_URL = basicProfileImageUrl;
    }

}
