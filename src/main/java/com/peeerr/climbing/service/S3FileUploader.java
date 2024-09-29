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
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public List<FileStoreDto> uploadFiles(List<MultipartFile> files) {
        return files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::uploadFile)
                .toList();
    }

    public FileStoreDto uploadFile(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
//            String fileType = checkFileType(file);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(file.getContentType());
            objectMetadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(bucket, filename, file.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ClimbingException(ErrorCode.FILE_STORE_FAILED);
        }

        return FileStoreDto.of(file.getOriginalFilename(), filename, amazonS3.getUrl(bucket, filename).toString());
    }

//    public void deleteFile(String fileName) {
//        amazonS3.deleteObject(bucket, fileName);
//    }
//

//    private String checkFileType(MultipartFile file) throws IOException {
//        String fileType = file.getContentType();
//
//        if (fileType != null && (fileType.equals(MediaType.IMAGE_JPEG_VALUE)
//                || fileType.equals(MediaType.IMAGE_PNG_VALUE)
//                || fileType.equals(MediaType.IMAGE_GIF_VALUE))) {
//            return fileType;
//        }
//
//        throw new ClimbingException(ErrorCode.INVALID_FILE_TYPE);
//    }

}
