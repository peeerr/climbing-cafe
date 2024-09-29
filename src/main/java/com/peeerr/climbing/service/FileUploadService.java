package com.peeerr.climbing.service;

import static com.peeerr.climbing.constant.Topic.FILE_CHUNK;
import static com.peeerr.climbing.constant.Topic.FILE_STATUS;

import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.dto.FileStatusMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final int CHUNK_SIZE = 750 * 1024;  // 750KB

    @Transactional
    public List<String> initiateFileUpload(Long loginId, Long postId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileId = UUID.randomUUID().toString();
            fileIds.add(fileId);

            sendFileStatus(fileId, FileUploadState.INITIATED);
            sendFileChunks(loginId, postId, fileId, file);
        }

        return fileIds;
    }

    private void sendFileChunks(Long loginId, Long postId, String fileId, MultipartFile file) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] fileBytes = file.getBytes();
                int chunks = (int) Math.ceil((double) fileBytes.length / CHUNK_SIZE);

                for (int i = 0; i < chunks; i++) {
                    int start = i * CHUNK_SIZE;
                    int end = Math.min(fileBytes.length, (i + 1) * CHUNK_SIZE);
                    byte[] chunk = Arrays.copyOfRange(fileBytes, start, end);

                    FileChunkMessage message = FileChunkMessage.builder()
                            .loginId(loginId)
                            .postId(postId)
                            .fileId(fileId)
                            .fileName(file.getOriginalFilename())
                            .chunkIndex(i)
                            .totalChunks(chunks)
                            .data(chunk)
                            .build();

                    kafkaTemplate.send(FILE_CHUNK, fileId, message);
                }
                sendFileStatus(fileId, FileUploadState.CHUNKS_SENT);
            } catch (IOException e) {
                sendFileStatus(fileId, FileUploadState.FAILED);
                throw new ClimbingException(ErrorCode.FILE_CHUNK_UPLOAD_FAILED);
            }
        });
    }

    public void sendFileStatus(String fileId, FileUploadState state) {
        kafkaTemplate.send(FILE_STATUS, fileId, new FileStatusMessage(fileId, state));
    }

}
