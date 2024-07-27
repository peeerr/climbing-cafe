package com.peeerr.climbing.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FileStoreDto {

    private String originalFilename;
    private String filename;
    private String filePath;

    public static FileStoreDto of(String originalFilename, String filename, String filePath) {
        return new FileStoreDto(originalFilename, filename, filePath);
    }

}
