package com.pp.service.image;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AmazonImageUploadService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadImage(MultipartFile file, InputStream processedImage, long contentLength, String dirName) throws IOException {

        // 파일명 생성 (중복 방지)
        String originalFileName = file.getOriginalFilename();
        String fileName = dirName + "/" + UUID.randomUUID() + "_" + originalFileName;

        // S3에 업로드
        ObjectMetadata metadata = new ObjectMetadata();
        //metadata.setContentLength(file.getSize());
        metadata.setContentLength(contentLength);
        metadata.setContentType(file.getContentType());

//        try (InputStream inputStream = file.getInputStream()) {
//            amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
//                    .withCannedAcl(CannedAccessControlList.PublicRead));
//        }
        try{
                       amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, processedImage, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // S3 URL 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    public void deleteImage(String imageUrl) {
        String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        amazonS3Client.deleteObject(bucket, fileName);
    }
}