package com.modutaxi.api.common.s3;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.modutaxi.api.common.exception.BaseException;
import com.modutaxi.api.common.exception.errorcode.S3ErrorCode;
import com.modutaxi.api.common.s3.dto.S3Result;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class S3Service {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public S3Result uploadFile(MultipartFile multipartFile) {

        String fileName = createFileName(multipartFile.getOriginalFilename());

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new BaseException(S3ErrorCode.UPLOAD_ERROR);
        }
        return new S3Result(amazonS3Client.getUrl(bucket, fileName).toString(), fileName);
    }


    public void deleteFile(String fileName) {
        try{
            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, fileName));
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID() + "_" + fileName;
    }

}
