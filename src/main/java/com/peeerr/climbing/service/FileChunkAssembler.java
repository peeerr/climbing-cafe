package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.FileUploadState;
import com.peeerr.climbing.dto.FileChunkMessage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FileChunkAssembler {

    private final Map<String, List<FileChunkMessage>> chunkMap = new ConcurrentHashMap<>();
    private final FileService fileService;
    private final FileUploadService fileUploadService;

    @Transactional
    public void assembleChunk(FileChunkMessage message) {
        String fileId = message.getFileId();
        chunkMap.computeIfAbsent(fileId, k -> Collections.synchronizedList(new ArrayList<>())).add(message);

        if (isFileComplete(fileId)) {
            CompletableFuture.runAsync(() -> {
                try {
                    byte[] completeFile = assembleFile(fileId);
                    fileService.uploadFile(message.getLoginId(), message.getPostId(), message.getFileName(),
                            completeFile);
                    fileUploadService.updateFileStatus(fileId, FileUploadState.UPLOADED);
                } catch (Exception e) {
                    fileUploadService.updateFileStatus(fileId, FileUploadState.FAILED);
                } finally {
                    chunkMap.remove(fileId);
                }
            });
        }
    }

    private boolean isFileComplete(String fileId) {
        List<FileChunkMessage> chunks = chunkMap.get(fileId);
        if (chunks == null || chunks.isEmpty()) {
            return false;
        }
        int totalChunks = chunks.get(0).getTotalChunks();
        return chunks.size() == totalChunks;
    }

    private byte[] assembleFile(String fileId) {
        List<FileChunkMessage> chunks = chunkMap.get(fileId);
        chunks.sort(Comparator.comparingInt(FileChunkMessage::getChunkIndex));

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (FileChunkMessage chunk : chunks) {
            outputStream.write(chunk.getData(), 0, chunk.getData().length);
        }
        return outputStream.toByteArray();
    }

}
