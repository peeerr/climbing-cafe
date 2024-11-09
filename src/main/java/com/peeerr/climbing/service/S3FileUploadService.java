package com.peeerr.climbing.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.peeerr.climbing.config.S3Properties;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3FileUploadService {

    private final AmazonS3 amazonS3;
    private final S3Properties s3Properties;
    private final RetryTemplate retryTemplate;

    public String initiateMultipartUpload(String s3FileName, String contentType) {
        try {
            return retryTemplate.execute(retryContext -> {
                try {
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentType(contentType);

                    InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
                            s3Properties.bucket(),
                            s3FileName
                    )
                            .withObjectMetadata(metadata)
                            .withCannedACL(CannedAccessControlList.PublicRead);

                    return amazonS3.initiateMultipartUpload(initRequest).getUploadId();
                } catch (AmazonS3Exception e) {
                    handleS3Exception(e, "initiate", s3FileName, retryContext);
                    throw e;
                }
            });
        } catch (Exception e) {
            log.error("Failed to initiate multipart upload for file: {}", s3FileName, e);
            throw new ClimbingException(ErrorCode.MULTIPART_UPLOAD_FAILED);
        }
    }

    public UploadPartResult uploadPart(FileChunkMessage message, String uploadId) {
        try {
            return retryTemplate.execute(retryContext -> {
                try {
                    UploadPartRequest uploadRequest = new UploadPartRequest()
                            .withBucketName(s3Properties.bucket())
                            .withKey(message.getS3FileName())
                            .withUploadId(uploadId)
                            .withPartNumber(message.getChunkIndex() + 1)
                            .withInputStream(new ByteArrayInputStream(message.getData()))
                            .withPartSize(message.getData().length);

                    UploadPartResult result = amazonS3.uploadPart(uploadRequest);
                    log.debug("Successfully uploaded part. FileId: {}, Part: {}",
                            message.getFileId(), message.getChunkIndex() + 1);
                    return result;

                } catch (AmazonS3Exception e) {
                    handleS3Exception(e, "upload part", message.getFileId(), retryContext);
                    throw e;
                }
            });
        } catch (Exception e) {
            log.error("Failed to upload part. FileId: {}, Part: {}",
                    message.getFileId(), message.getChunkIndex(), e);
            throw new ClimbingException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    public String completeMultipartUpload(FileChunkMessage message, String uploadId, List<PartETag> partETags) {
        try {
            if (!validatePartETags(partETags, message.getTotalChunks())) {
                throw new ClimbingException(ErrorCode.INVALID_PART_STATE);
            }

            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                    s3Properties.bucket(),
                    message.getS3FileName(),
                    uploadId,
                    partETags
            );

            amazonS3.completeMultipartUpload(completeRequest);
            String fileUrl = amazonS3.getUrl(s3Properties.bucket(), message.getS3FileName()).toString();

            log.info("Successfully completed multipart upload. FileId: {}", message.getFileId());
            return fileUrl;  // fileUrl 반환하도록 변경

        } catch (ClimbingException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to complete multipart upload. FileId: {}", message.getFileId(), e);
            throw new ClimbingException(ErrorCode.MULTIPART_UPLOAD_FAILED);
        }
    }

    public void abortMultipartUpload(String s3FileName, String uploadId) {
        if (uploadId == null) {
            return;
        }

        try {
            AbortMultipartUploadRequest abortRequest = new AbortMultipartUploadRequest(
                    s3Properties.bucket(),
                    s3FileName,
                    uploadId
            );

            amazonS3.abortMultipartUpload(abortRequest);
            log.info("Successfully aborted multipart upload. FileName: {}", s3FileName);

        } catch (Exception e) {
            log.error("Failed to abort multipart upload. FileName: {}", s3FileName, e);
        }
    }

    private void handleS3Exception(AmazonS3Exception e,
                                   String operation,
                                   String fileId,
                                   RetryContext retryContext) {
        int retryCount = retryContext.getRetryCount();

        switch (e.getStatusCode()) {
            case 400:
                log.error("Invalid S3 {} request. FileId: {}", operation, fileId);
                throw new ClimbingException(ErrorCode.S3_INVALID_REQUEST);

            case 403:
                log.error("S3 permission denied during {}. FileId: {}", operation, fileId);
                throw new ClimbingException(ErrorCode.S3_PERMISSION_DENIED);

            case 404:
                log.error("S3 resource not found during {}. FileId: {}", operation, fileId);
                throw new ClimbingException(ErrorCode.S3_RESOURCE_NOT_FOUND);

            case 500:
            case 503:
                log.warn("Temporary S3 service issue during {}. Retry: {}, FileId: {}",
                        operation, retryCount + 1, fileId);
                // 재시도 가능하므로 예외를 다시 던짐
                break;

            default:
                log.error("Unexpected S3 error during {}. Status: {}, FileId: {}",
                        operation, e.getStatusCode(), fileId);
                throw new ClimbingException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }

    private boolean validatePartETags(List<PartETag> partETags, int totalParts) {
        if (partETags.size() != totalParts) {
            return false;
        }

        return IntStream.rangeClosed(1, totalParts)
                .allMatch(partNumber ->
                        partETags.stream()
                                .anyMatch(tag -> tag.getPartNumber() == partNumber)
                );
    }

}
