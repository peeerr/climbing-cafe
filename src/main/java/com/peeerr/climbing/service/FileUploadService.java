package com.peeerr.climbing.service;

import com.peeerr.climbing.constant.FileUploadMetadata;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileUploadService {

    private static final int CHUNK_SIZE = 5 * 1024 * 1024; // 5MB

    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @Transactional
    public List<String> uploadFiles(Long loginId, Long postId, List<MultipartFile> files) {
        try {
            return files.stream()
                    .map(file -> initiateSingleFileUpload(loginId, postId, file))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
        } catch (Exception e) {
            log.error("Failed to process file uploads. PostId: {}", postId, e);
            throw new ClimbingException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private Optional<String> initiateSingleFileUpload(Long loginId, Long postId, MultipartFile file) {
        String fileId = UUID.randomUUID().toString();

        try {
            validateFile(file);

            FileUploadMetadata metadata = createFileMetadata(fileId, file);
            initializeUpload(metadata);
            sendFileChunks(loginId, postId, metadata, file);

            return Optional.of(fileId);
        } catch (Exception e) {
            handleUploadFailure(fileId, e);
            return Optional.empty();
        }
    }

    private FileUploadMetadata createFileMetadata(String fileId, MultipartFile file) {
        return FileUploadMetadata.builder()
                .fileId(fileId)
                .originalFileName(file.getOriginalFilename())
                .s3FileName(fileId + "_" + file.getOriginalFilename())
                .contentType(Optional.ofNullable(file.getContentType())
                        .orElse("application/octet-stream"))
                .build();
    }

    private void validateFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !isValidContentType(contentType)) {
            throw new ClimbingException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private boolean isValidContentType(String contentType) {
        return contentType.startsWith("image/jpeg") ||
                contentType.startsWith("image/png") ||
                contentType.startsWith("image/gif");
    }

    private void initializeUpload(FileUploadMetadata metadata) {
        String uploadId = s3FileUploadService.initiateMultipartUpload(
                metadata.getS3FileName(),
                metadata.getContentType()
        );
        messagingService.saveUploadIdToRedis(metadata.getFileId(), uploadId);
        messagingService.sendFileStatus(metadata.getFileId(), FileUploadState.INITIATED);
    }

    private void sendFileChunks(Long loginId,
                                Long postId,
                                FileUploadMetadata metadata,
                                MultipartFile file) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] fileBytes = file.getBytes();
                int totalChunks = calculateTotalChunks(fileBytes.length);

                for (int chunkIndex = 0; chunkIndex < totalChunks; chunkIndex++) {
                    byte[] chunkData = extractChunkData(fileBytes, chunkIndex);
                    sendChunk(createChunkMessage(loginId, postId, metadata, chunkIndex, totalChunks, chunkData));
                }

                messagingService.sendFileStatus(metadata.getFileId(), FileUploadState.CHUNKS_SENT);
                log.info("Successfully sent all chunks. FileId: {}, TotalChunks: {}",
                        metadata.getFileId(), totalChunks);

            } catch (Exception e) {
                log.error("Failed to process file chunks. FileId: {}", metadata.getFileId(), e);
                handleChunkingFailure(metadata.getFileId(), metadata.getS3FileName());
            }
        }).exceptionally(throwable -> {
            log.error("Async chunk processing failed. FileId: {}", metadata.getFileId(), throwable);
            handleChunkingFailure(metadata.getFileId(), metadata.getS3FileName());
            return null;
        });
    }

    private int calculateTotalChunks(long fileSize) {
        return (int) Math.ceil((double) fileSize / CHUNK_SIZE);
    }

    private byte[] extractChunkData(byte[] fileBytes, int chunkIndex) {
        int start = chunkIndex * CHUNK_SIZE;
        int end = Math.min(fileBytes.length, (chunkIndex + 1) * CHUNK_SIZE);
        return Arrays.copyOfRange(fileBytes, start, end);
    }

    private void sendChunk(FileChunkMessage message) {
        try {
            messagingService.sendFileChunkToKafka(message);
            log.debug("Sent chunk {}/{} for fileId: {}",
                    message.getChunkIndex() + 1, message.getTotalChunks(), message.getFileId());
        } catch (Exception e) {
            log.error("Failed to send chunk. FileId: {}, ChunkIndex: {}",
                    message.getFileId(), message.getChunkIndex(), e);
            throw new ClimbingException(ErrorCode.FILE_CHUNK_UPLOAD_FAILED);
        }
    }

    private FileChunkMessage createChunkMessage(Long loginId,
                                                Long postId,
                                                FileUploadMetadata metadata,
                                                int chunkIndex,
                                                int totalChunks,
                                                byte[] chunkData) {
        return FileChunkMessage.builder()
                .loginId(loginId)
                .postId(postId)
                .fileId(metadata.getFileId())
                .originalFileName(metadata.getOriginalFileName())
                .s3FileName(metadata.getS3FileName())
                .contentType(metadata.getContentType())
                .chunkIndex(chunkIndex)
                .totalChunks(totalChunks)
                .data(chunkData)
                .build();
    }

    private void handleUploadFailure(String fileId, Exception e) {
        log.error("Failed to initiate file upload. FileId: {}", fileId, e);
        try {
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
            messagingService.cleanupRedisKeys(fileId);
        } catch (Exception cleanupError) {
            log.error("Failed to cleanup after upload failure. FileId: {}", fileId, cleanupError);
        }
    }

    private void handleChunkingFailure(String fileId, String s3FileName) {
        try {
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
            messagingService.cleanupRedisKeys(fileId);

            messagingService.getUploadIdFromRedis(fileId)
                    .ifPresent(uploadId ->
                            s3FileUploadService.abortMultipartUpload(s3FileName, uploadId));
        } catch (Exception e) {
            log.error("Failed to cleanup after chunking failure. FileId: {}", fileId, e);
        }
    }

}
