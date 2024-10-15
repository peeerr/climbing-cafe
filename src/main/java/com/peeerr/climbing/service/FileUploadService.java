package com.peeerr.climbing.service;

import static com.peeerr.climbing.constant.FileUploadConstants.CHUNK_SIZE;

import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.dto.FileChunkMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @Transactional
    public List<String> uploadFiles(Long loginId, Long postId, List<MultipartFile> files) {
        return files.stream()
                .map(file -> initiateSingleFileUpload(loginId, postId, file))
                .toList();
    }

    private String initiateSingleFileUpload(Long loginId, Long postId, MultipartFile file) {
        String fileId = UUID.randomUUID().toString();
        String originalFileName = file.getOriginalFilename();
        String s3FileName = fileId + "_" + originalFileName;
        String contentType = Optional.ofNullable(file.getContentType()).orElse("application/octet-stream");

        try {
            String uploadId = s3FileUploadService.initiateMultipartUpload(s3FileName, contentType);
            messagingService.saveUploadIdToRedis(fileId, uploadId);
            sendFileChunks(loginId, postId, fileId, s3FileName, originalFileName, contentType, file);
            messagingService.sendFileStatus(fileId, FileUploadState.INITIATED);
            return fileId;
        } catch (Exception e) {
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
            throw new RuntimeException("Failed to initiate file upload", e);
        }
    }

    private void sendFileChunks(Long loginId, Long postId, String fileId, String s3FileName, String originalFileName,
                                String contentType, MultipartFile file) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] fileBytes = file.getBytes();
                int totalChunks = (int) Math.ceil((double) fileBytes.length / CHUNK_SIZE);

                IntStream.range(0, totalChunks).forEach(chunkIndex -> {
                    byte[] chunkBytes = Arrays.copyOfRange(fileBytes, chunkIndex * CHUNK_SIZE,
                            Math.min(fileBytes.length, (chunkIndex + 1) * CHUNK_SIZE));

                    FileChunkMessage message = new FileChunkMessage(loginId, postId, fileId, originalFileName,
                            s3FileName, contentType, chunkIndex, totalChunks, chunkBytes);
                    messagingService.sendFileChunkToKafka(message);
                });

                messagingService.sendFileStatus(fileId, FileUploadState.CHUNKS_SENT);
            } catch (IOException e) {
                messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
                throw new RuntimeException("Failed to send file chunks", e);
            }
        });
    }

}
