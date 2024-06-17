package com.modutaxi.api.common.s3;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.S3ErrorCode;
import com.modutaxi.api.common.s3.dto.S3Response.S3DeleteResponse;
import com.modutaxi.api.common.s3.dto.S3Response.S3UploadResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3Client amazonS3Client;
    private final Environment environment;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.cloudfront.s3.url}")
    private String cloudfrontUrl;

    public S3UploadResponse uploadFile(MultipartFile multipartFile, S3ObjectType s3ObjectType) {
        String fileName = createFileName(multipartFile.getOriginalFilename(), s3ObjectType);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentDisposition("inline");

        List<Tag> tagList = new ArrayList<>();
        switch (s3ObjectType) {
            case PROFILE:
                tagList.add(new Tag("type", "profile"));
                fileName = "profile/" + fileName;
                break;
            case MESSAGE:
                tagList.add(new Tag("type", "message"));
                fileName = "message/" + fileName;
                break;
            case EXT:
                tagList.add(new Tag("type", "ext"));
                fileName = "ext/" + fileName;
                break;
        }
        ObjectTagging objectTagging = new ObjectTagging(tagList);

        fileName = (environment.getProperty("spring.profiles.active") == null ? "local" : environment.getProperty("spring.profiles.active")) + "/" + fileName;

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(
                new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.Private)
                    .withTagging(objectTagging)
            );
        } catch (SdkClientException | IOException e) {
            throw new BaseException(S3ErrorCode.UPLOAD_ERROR);
        }
        return new S3UploadResponse(cloudfrontUrl + parseFileName(amazonS3Client.getUrl(bucket, fileName).toString()), fileName);
    }

    public S3DeleteResponse deleteFile(String imageUrl) {
        String fileName = parseFileName(imageUrl);
        try {
            if (amazonS3Client.doesObjectExist(bucket, fileName)) {
                amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
                return new S3DeleteResponse(true);
            } else {
                throw new BaseException(S3ErrorCode.NOT_EXIST_FILE);
            }
        } catch (SdkClientException e) {
            throw new BaseException(S3ErrorCode.AWS_CONNECTION_ERROR);
        }
    }

    private String createFileName(String fileName, S3ObjectType s3ObjectType) {
        switch (s3ObjectType) {
            case PROFILE:
                return "MT_P_"
                    + LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS-"))
                    + RandomStringUtils.random(2, true, true)
                    + fileName.substring(fileName.lastIndexOf("."));
            case MESSAGE:
                return "MT_M_"
                    + LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS-"))
                    + RandomStringUtils.random(2, true, true)
                    + fileName.substring(fileName.lastIndexOf("."));
            case EXT:
                return "MT_E_"
                    + LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss.SSS-"))
                    + RandomStringUtils.random(2, true, true)
                    + fileName.substring(fileName.lastIndexOf("."));
            default:
                throw new BaseException(S3ErrorCode.OBJECT_TYPE_ERROR);
        }
    }

    private String parseFileName(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf('/', cloudfrontUrl.length() - 1) + 1);
    }
}
