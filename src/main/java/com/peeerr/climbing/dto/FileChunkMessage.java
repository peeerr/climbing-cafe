package com.peeerr.climbing.dto;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FileChunkMessage implements Serializable {

    private Long loginId;
    private Long postId;
    private String fileId;
    private String fileName;
    private int chunkIndex;
    private int totalChunks;
    private byte[] data;

    @Builder
    public FileChunkMessage(Long loginId, Long postId, String fileId, String fileName, int chunkIndex, int totalChunks,
                            byte[] data) {
        this.loginId = loginId;
        this.postId = postId;
        this.fileId = fileId;
        this.fileName = fileName;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
        this.data = data;
    }
    
}
