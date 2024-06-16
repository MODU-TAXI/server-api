package com.modutaxi.api.common.s3;

import com.modutaxi.api.common.s3.dto.S3Response.S3DeleteResponse;
import com.modutaxi.api.common.s3.dto.S3Response.S3UploadResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<S3UploadResponse> uploadFile(
        @RequestPart(value = "file") MultipartFile file,
        @Parameter(description = "파일 타입") @RequestParam(defaultValue = "EXT") S3ObjectType s3ObjectType) {
        return ResponseEntity.ok(s3Service.uploadFile(file, s3ObjectType));
    }

    @DeleteMapping
    public ResponseEntity<S3DeleteResponse> deleteFile(@Parameter(description = "삭제할 파일 주소") @RequestParam(value = "imageUrl") String imageUrl) {
        return ResponseEntity.ok(s3Service.deleteFile(imageUrl));
    }
}
