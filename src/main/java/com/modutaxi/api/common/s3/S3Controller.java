package com.modutaxi.api.common.s3;

import com.modutaxi.api.common.s3.dto.S3Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/s3")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<S3Result> uploadFile(@RequestPart(value = "file") MultipartFile file) {
        S3Result result = s3Service.uploadFile(file);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteFile(@RequestParam String fileName){
        s3Service.deleteFile(fileName);
        return ResponseEntity.ok("파일을 삭제하였습니다.");
    }
}
