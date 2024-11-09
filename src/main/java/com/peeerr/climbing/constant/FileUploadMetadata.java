package com.peeerr.climbing.constant;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class FileUploadMetadata {

    private final String fileId;
    private final String originalFileName;
    private final String s3FileName;
    private final String contentType;
    
}
