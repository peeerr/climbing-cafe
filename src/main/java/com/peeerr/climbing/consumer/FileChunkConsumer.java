package com.peeerr.climbing.consumer;

import com.amazonaws.services.s3.model.UploadPartResult;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.constant.Topic;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.service.FileStorageService;
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

    private final FileStorageService fileStorageService;
    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @KafkaListener(topics = Topic.FILE_CHUNK, groupId = "file-upload", concurrency = "3")
    public void handleFileChunk(FileChunkMessage message) {
        String fileId = message.getFileId();

        // 이미 실패한 파일인지 확인
        if (messagingService.isFailedFileId(fileId)) {
            log.info("Skipping chunk for failed file. FileId: {}, ChunkIndex: {}",
                    fileId, message.getChunkIndex());
            return;
        }

        try {
            processChunk(message);
        } catch (Exception e) {
            log.error("Failed to process chunk. FileId: {}, ChunkIndex: {}",
                    fileId, message.getChunkIndex(), e);
            handleChunkProcessingFailure(message);
            throw e;  // ErrorHandler 가 처리하도록 다시 throw
        }
    }

    private void processChunk(FileChunkMessage message) {
        String fileId = message.getFileId();

        // uploadId 조회
        String uploadId = messagingService.getUploadIdFromRedis(fileId)
                .orElseThrow(() -> {
                    log.error("Upload ID not found. FileId: {}", fileId);
                    return new ClimbingException(ErrorCode.FILE_STATUS_NOT_FOUND);
                });

        // S3에 청크 업로드
        UploadPartResult result = s3FileUploadService.uploadPart(message, uploadId);

        // 성공한 파트 정보 저장
        messagingService.savePartETagToRedis(
                fileId,
                message.getChunkIndex() + 1,
                result.getPartETag()
        );

        // 상태 업데이트
        messagingService.sendFileStatus(fileId, FileUploadState.PART_UPLOADED);

        // 모든 청크가 완료되었는지 확인
        if (isUploadComplete(message)) {
            fileStorageService.completeUpload(message, uploadId);
        }
    }

    private boolean isUploadComplete(FileChunkMessage message) {
        return messagingService.isUploadComplete(
                message.getFileId(),
                message.getTotalChunks()
        );
    }

    private void handleChunkProcessingFailure(FileChunkMessage message) {
        String fileId = message.getFileId();
        try {
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
            messagingService.recordFailedFileId(fileId);

            messagingService.getUploadIdFromRedis(fileId)
                    .ifPresent(uploadId -> {
                        s3FileUploadService.abortMultipartUpload(message.getS3FileName(), uploadId);
                        messagingService.cleanupRedisKeys(fileId);
                    });
        } catch (Exception e) {
            log.error("Failed to handle chunk processing failure. FileId: {}", fileId, e);
        }
    }

}
