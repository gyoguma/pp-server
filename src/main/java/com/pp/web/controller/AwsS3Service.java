package com.pp.web.controller;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class AwsS3Service {
    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadAndProcessImage(MultipartFile file) throws Exception {
        // 1. Extract metadata from the original image
        Metadata originalMetadata = ImageMetadataReader.readMetadata(file.getInputStream());

        // 2. Process the image (resize in this example)
       // InputStream processedImageStream = processImage(file.getInputStream());

        // 3. Prepare metadata for S3
        ObjectMetadata s3Metadata = new ObjectMetadata();
        s3Metadata.setContentType(file.getContentType());

        // 5. Generate a unique key for the image in S3
        String key = "processed-images/" + java.util.UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 6. Upload to S3 (without user metadata)
        amazonS3Client.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), s3Metadata));

        return key;
    }

//    private InputStream processImage(InputStream originalImageStream) throws IOException {
//        // Example: Resize to 300x300 using Thumbnailator
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        Thumbnails.of(originalImageStream)
//                .size(300, 300)
//                .toOutputStream(outputStream);
//        return new ByteArrayInputStream(outputStream.toByteArray());
//    }

    private Map<String, String> extractUserMetadata(Metadata metadata) {
        Map<String, String> userMetadata = new HashMap<>();
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                // Customize metadata extraction as needed
                String tagName = tag.getTagName().replaceAll("[^a-zA-Z0-9]", ""); // Remove special characters
                String tagValue = tag.getDescription();

            }
        }
        return userMetadata;
    }
}