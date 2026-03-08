package com.jp.be_jplearning.integration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class AwsS3Client {

    private final S3Client s3Client;

    @Value("${aws.bucket}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    public String uploadAudioBytes(String fileName, byte[] audioBytes) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("audio/" + fileName)
                    .contentType("audio/mpeg")
                    // Note: If you need it completely public using ACL and have not enforced bucket
                    // owner block:
                    // .acl(ObjectCannedACL.PUBLIC_READ)
                    .build();

            log.info("Uploading audio {} to bucket {}", fileName, bucketName);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(audioBytes));

            String url = String.format("https://%s.s3.%s.amazonaws.com/audio/%s", bucketName, region, fileName);
            log.info("Successfully uploaded file to S3: {}", url);
            return url;

        } catch (S3Exception e) {
            log.error("Failed to upload audio to S3", e);
            throw new RuntimeException("S3 Upload Error: " + e.getMessage(), e);
        }
    }

    public String uploadImageBytes(String fileName, byte[] imageBytes, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key("avatars/" + fileName)
                    .contentType(contentType)
                    .build();

            log.info("Uploading image {} to bucket {}", fileName, bucketName);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(imageBytes));

            String url = String.format("https://%s.s3.%s.amazonaws.com/avatars/%s", bucketName, region, fileName);
            log.info("Successfully uploaded image to S3: {}", url);
            return url;

        } catch (S3Exception e) {
            log.error("Failed to upload image to S3", e);
            throw new RuntimeException("S3 Upload Error: " + e.getMessage(), e);
        }
    }

    public void deleteImage(String fileName) {
        try {
            software.amazon.awssdk.services.s3.model.DeleteObjectRequest deleteObjectRequest = software.amazon.awssdk.services.s3.model.DeleteObjectRequest
                    .builder()
                    .bucket(bucketName)
                    .key("avatars/" + fileName)
                    .build();

            log.info("Deleting image {} from bucket {}", fileName, bucketName);
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Successfully deleted image from S3: {}", fileName);

        } catch (S3Exception e) {
            log.error("Failed to delete image from S3", e);
            throw new RuntimeException("S3 Delete Error: " + e.getMessage(), e);
        }
    }
}
