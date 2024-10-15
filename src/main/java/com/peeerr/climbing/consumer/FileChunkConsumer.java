package com.peeerr.climbing.consumer;

import com.amazonaws.services.s3.model.UploadPartResult;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.constant.Topic;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.service.FileUploadMessagingService;
import com.peeerr.climbing.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FileChunkConsumer {

    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @KafkaListener(topics = Topic.FILE_CHUNK, groupId = "file-upload", concurrency = "3")
    public void handleFileChunk(FileChunkMessage message) {
        messagingService.getUploadIdFromRedis(message.getFileId())
                .ifPresentOrElse(
                        uploadId -> processChunkWithUploadId(message, uploadId),
                        () -> handleMissingUploadId(message.getFileId())
                );
    }

    private void processChunkWithUploadId(FileChunkMessage message, String uploadId) {
        try {
            processFileChunk(message, uploadId);
            if (isLastChunk(message)) {
                completeUpload(message, uploadId);
            }
        } catch (Exception e) {
            handleUploadFailure(message.getFileId(), message, uploadId);
        }
    }

    private void handleMissingUploadId(String fileId) {
        updateFileStatus(fileId, FileUploadState.FAILED);
    }

    private void processFileChunk(FileChunkMessage message, String uploadId) {
        UploadPartResult uploadPartResult = s3FileUploadService.uploadPart(message, uploadId);
        messagingService.savePartETagToRedis(message.getFileId(), message.getChunkIndex() + 1,
                uploadPartResult.getPartETag());
        updateFileStatus(message.getFileId(), FileUploadState.PART_UPLOADED);
    }

    private boolean isLastChunk(FileChunkMessage message) {
        return message.getChunkIndex() == message.getTotalChunks() - 1;
    }

    private void completeUpload(FileChunkMessage message, String uploadId) {
        s3FileUploadService.completeMultipartUpload(message, uploadId);
        updateFileStatus(message.getFileId(), FileUploadState.COMPLETED);
        messagingService.cleanupRedisKeys(message.getFileId());
    }

    private void handleUploadFailure(String fileId, FileChunkMessage message, String uploadId) {
        updateFileStatus(fileId, FileUploadState.FAILED);
        s3FileUploadService.abortMultipartUpload(message, uploadId);
    }

    private void updateFileStatus(String fileId, FileUploadState state) {
        messagingService.sendFileStatus(fileId, state);
    }

}
