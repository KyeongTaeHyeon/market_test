package com.jpa.market.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeletedObject;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    @Value("$spring.colud.aws.region.static")
    private String region;

    public String uploadFile(String folder, String originalFileName, byte[] fileData) throws Exception {
        // uuid를 이용하여 고유한 파일 이름을 생성하기 위해 이용
        UUID uuid = UUID.randomUUID();

        // 확장자 추출
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        // uuid와 확장자와 결합하여 저장할 파일명 생성
        String savedFileName = uuid + extension;

        //
        String key = folder + "/" + savedFileName;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("image/jpeg")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileData));

        return getUploadUrl(key);
    }

    private String getUploadUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);
    }

    // 등록된 파일 삭제
    public void deleteFile(String key) throws Exception {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
        System.out.println(key + " : S3에서 파일 삭제 완료");
    }
}
