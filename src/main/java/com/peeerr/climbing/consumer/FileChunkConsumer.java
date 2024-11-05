package com.peeerr.climbing.consumer;

import com.amazonaws.services.s3.model.UploadPartResult;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.constant.Topic;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.service.FileUploadMessagingService;
import com.peeerr.climbing.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileChunkConsumer {

    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @KafkaListener(topics = Topic.FILE_CHUNK, groupId = "file-upload", concurrency = "3")
    public void handleFileChunk(FileChunkMessage message) {
        if (messagingService.isFailedFileId(message.getFileId())) {
            log.info("Skipping chunk for failed file upload. FileId: {}", message.getFileId());
            return;
        }

        try {
            processChunk(message);
        } catch (Exception e) {
            log.error("Error processing chunk for fileId: {}", message.getFileId(), e);
            throw e;  // ErrorHandler가 처리하도록 다시 던짐
        }
    }

    private void processChunk(FileChunkMessage message) {
        messagingService.getUploadIdFromRedis(message.getFileId())
                .ifPresentOrElse(
                        uploadId -> processChunkWithUploadId(message, uploadId),
                        () -> handleMissingUploadId(message.getFileId())
                );
    }

    private void processChunkWithUploadId(FileChunkMessage message, String uploadId) {
        try {
            UploadPartResult result = s3FileUploadService.uploadPart(message, uploadId);
            messagingService.savePartETagToRedis(message.getFileId(), message.getChunkIndex() + 1,
                    result.getPartETag());
            messagingService.sendFileStatus(message.getFileId(), FileUploadState.PART_UPLOADED);

            if (isUploadComplete(message)) {
                completeUpload(message, uploadId);
            }
        } catch (Exception e) {
            log.error("Failed to process chunk with uploadId. FileId: {}, UploadId: {}",
                    message.getFileId(), uploadId, e);
            throw e;
        }
    }

    private boolean isUploadComplete(FileChunkMessage message) {
        return messagingService.isUploadComplete(message.getFileId(), message.getTotalChunks());
    }

    private void completeUpload(FileChunkMessage message, String uploadId) {
        s3FileUploadService.completeMultipartUpload(message, uploadId);
        messagingService.sendFileStatus(message.getFileId(), FileUploadState.COMPLETED);
        messagingService.cleanupRedisKeys(message.getFileId());
    }

    private void handleMissingUploadId(String fileId) {
        log.error("Upload ID not found for fileId: {}", fileId);
        messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
        throw new IllegalStateException("Upload ID not found");
    }

}
