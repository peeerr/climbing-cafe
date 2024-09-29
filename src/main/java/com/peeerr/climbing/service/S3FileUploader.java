package com.peeerr.climbing.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.peeerr.climbing.dto.FileStoreDto;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class S3FileUploader {

    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.accessKey}")
    private String accessKey;

    @Value("${aws.s3.secretKey}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @PostConstruct
    private void s3Client() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);

        amazonS3 = AmazonS3ClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

    public List<String> getFiles(List<String> filenames) {
        return filenames.stream()
                .map(filename -> amazonS3.getUrl(bucket, filename).toString())
                .toList();
    }

    public FileStoreDto uploadFile(String originalFilename, byte[] fileData) {
        String filename = UUID.randomUUID() + "_" + originalFilename;

        try {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(fileData.length);

            String contentType = URLConnection.guessContentTypeFromName(originalFilename);
            if (contentType != null) {
                objectMetadata.setContentType(contentType);
            }

            ByteArrayInputStream inputStream = new ByteArrayInputStream(fileData);
            amazonS3.putObject(new PutObjectRequest(bucket, filename, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new ClimbingException(ErrorCode.FILE_STORE_FAILED);
        }

        return FileStoreDto.of(originalFilename, filename, amazonS3.getUrl(bucket, filename).toString());
    }

}
