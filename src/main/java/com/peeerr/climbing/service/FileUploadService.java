package com.peeerr.climbing.service;

import static com.peeerr.climbing.constant.Topic.FILE_CHUNK;

import com.peeerr.climbing.domain.FileUploadState;
import com.peeerr.climbing.domain.FileUploadStatus;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.FileUploadStatusRepository;
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
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
public class FileUploadService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final FileUploadStatusRepository fileUploadStatusRepository;
    private final TransactionTemplate transactionTemplate;

    private static final int CHUNK_SIZE = 750 * 1024;  // 750KB

    @Transactional
    public List<String> initiateFileUpload(Long loginId, Long postId, List<MultipartFile> files) {
        List<String> fileIds = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileId = UUID.randomUUID().toString();
            fileIds.add(fileId);

            fileUploadStatusRepository.save(new FileUploadStatus(fileId, FileUploadState.INITIATED));
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
                updateFileStatus(fileId, FileUploadState.CHUNKS_SENT);
            } catch (IOException e) {
                updateFileStatus(fileId, FileUploadState.FAILED);
                throw new ClimbingException(ErrorCode.FILE_CHUNK_UPLOAD_FAILED);
            }
        });
    }

    public void updateFileStatus(String fileId, FileUploadState state) {
        transactionTemplate.execute(status -> {
            fileUploadStatusRepository.updateStatus(fileId, state);
            return null;
        });
    }

    @Transactional(readOnly = true)
    public FileUploadStatus getFileUploadStatus(String fileId) {
        return fileUploadStatusRepository.findById(fileId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.FILE_STATUS_NOT_FOUND));
    }

}
