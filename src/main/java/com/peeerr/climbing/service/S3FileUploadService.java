package com.peeerr.climbing.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.peeerr.climbing.config.S3Properties;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.domain.File;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.repository.PostRepository;
import java.io.ByteArrayInputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class S3FileUploadService {

    private final AmazonS3 amazonS3;
    private final FileUploadMessagingService messagingService;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final S3Properties s3Properties;

    public String initiateMultipartUpload(String s3FileName, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);

        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(s3Properties.bucket(),
                s3FileName)
                .withObjectMetadata(metadata)
                .withCannedACL(CannedAccessControlList.PublicRead);

        return amazonS3.initiateMultipartUpload(initRequest).getUploadId();
    }

    public UploadPartResult uploadPart(FileChunkMessage message, String uploadId) {
        UploadPartRequest uploadRequest = new UploadPartRequest()
                .withBucketName(s3Properties.bucket())
                .withKey(message.getS3FileName())
                .withUploadId(uploadId)
                .withPartNumber(message.getChunkIndex() + 1)
                .withInputStream(new ByteArrayInputStream(message.getData()))
                .withPartSize(message.getData().length);

        return amazonS3.uploadPart(uploadRequest);
    }

    public void completeMultipartUpload(FileChunkMessage message, String uploadId) {
        List<PartETag> partETags = messagingService.getPartETagsFromRedis(message.getFileId(),
                message.getTotalChunks());

        CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(
                s3Properties.bucket(), message.getS3FileName(), uploadId, partETags);

        amazonS3.completeMultipartUpload(completeRequest);

        String fileUrl = amazonS3.getUrl(s3Properties.bucket(), message.getS3FileName()).toString();
        saveFileInfo(message, fileUrl);
        messagingService.cleanupRedisKeys(message.getFileId());
        messagingService.sendFileStatus(message.getFileId(), FileUploadState.COMPLETED);
    }

    public void abortMultipartUpload(FileChunkMessage message, String uploadId) {
        if (uploadId != null) {
            amazonS3.abortMultipartUpload(
                    new AbortMultipartUploadRequest(s3Properties.bucket(), message.getS3FileName(), uploadId));
        }
    }

    private void saveFileInfo(FileChunkMessage message, String fileUrl) {
        Post post = postRepository.findById(message.getPostId())
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));

        File fileEntity = File.builder()
                .post(post)
                .originalFilename(message.getOriginalFileName())
                .filename(message.getS3FileName())
                .filePath(fileUrl)
                .build();

        fileRepository.save(fileEntity);
    }

}
