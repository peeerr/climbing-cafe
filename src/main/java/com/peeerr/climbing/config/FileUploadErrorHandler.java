package com.peeerr.climbing.config;

import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.service.FileUploadMessagingService;
import com.peeerr.climbing.service.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileUploadErrorHandler {

    private final FileUploadMessagingService messagingService;
    private final S3FileUploadService s3FileUploadService;

    public void handleError(ConsumerRecord<?, ?> record, Exception exception) {
        try {
            FileChunkMessage message = (FileChunkMessage) record.value();
            String fileId = message.getFileId();

            log.error("File chunk processing failed. FileId: {}, ChunkIndex: {}, Error: {}",
                    fileId, message.getChunkIndex(), exception.getMessage());

            handleFailedUpload(message);
        } catch (Exception e) {
            log.error("Error occurred while handling failure", e);
        }
    }

    private void handleFailedUpload(FileChunkMessage message) {
        String fileId = message.getFileId();

        try {
            // 1. 실패 상태 업데이트
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);

            // 2. S3 리소스 정리
            cleanupS3Resources(message);

            // 3. Redis 키 정리
            messagingService.cleanupRedisKeys(fileId);

            // 4. 실패한 파일 ID 기록
            messagingService.recordFailedFileId(fileId);

        } catch (Exception e) {
            log.error("Failed to handle upload failure for fileId: {}", fileId, e);
        }
    }

    private void cleanupS3Resources(FileChunkMessage message) {
        messagingService.getUploadIdFromRedis(message.getFileId())
                .ifPresent(uploadId -> {
                    try {
                        s3FileUploadService.abortMultipartUpload(message, uploadId);
                        log.info("Successfully aborted multipart upload. FileId: {}, UploadId: {}",
                                message.getFileId(), uploadId);
                    } catch (Exception e) {
                        log.error("Failed to abort multipart upload. FileId: {}, UploadId: {}",
                                message.getFileId(), uploadId, e);
                    }
                });
    }

}
