package com.peeerr.climbing.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.peeerr.climbing.dto.file.FileStoreDto;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.FileStoreException;
import com.peeerr.climbing.exception.ex.FileTypeException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public List<FileStoreDto> uploadFiles(List<MultipartFile> files) {
        List<FileStoreDto> storeFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                storeFiles.add(uploadFile(file));
            }
        }

        return storeFiles;
    }

    public FileStoreDto uploadFile(MultipartFile file) {
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();

        try {
            String fileType = checkFileType(file);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(fileType);

            amazonS3.putObject(new PutObjectRequest(bucket, filename, file.getInputStream(), objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FileStoreException(ErrorMessage.FILE_STORE_FAILED);
        }

        return FileStoreDto.of(file.getOriginalFilename(), filename, amazonS3.getUrl(bucket, filename).toString());
    }

//    public void deleteFile(String fileName) {
//        amazonS3.deleteObject(bucket, fileName);
//    }

    public String checkFileType(MultipartFile file) throws IOException {
        String fileType = new Tika().detect(file.getInputStream());

        if (MediaType.IMAGE_JPEG_VALUE.equals(fileType) || MediaType.IMAGE_PNG_VALUE.equals(fileType) || MediaType.IMAGE_GIF_VALUE.equals(fileType)) {
            return fileType;
        }

        throw new FileTypeException(ErrorMessage.INVALID_FILE_TYPE);
    }

}
