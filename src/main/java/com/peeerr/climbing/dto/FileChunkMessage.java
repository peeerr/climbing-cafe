package com.peeerr.climbing.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FileChunkMessage implements Serializable {

    private Long loginId;
    private Long postId;
    private String fileId;
    private String originalFileName;
    private String s3FileName;
    private String contentType;
    private int chunkIndex;
    private int totalChunks;
    private byte[] data;

}
