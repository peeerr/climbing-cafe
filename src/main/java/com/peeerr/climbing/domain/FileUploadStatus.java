package com.peeerr.climbing.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class FileUploadStatus {
    @Id
    private String fileId;

    @Enumerated(EnumType.STRING)
    private FileUploadState state;

    private LocalDateTime updatedAt;

    public FileUploadStatus(String fileId, FileUploadState state) {
        this.fileId = fileId;
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateState(FileUploadState state) {
        this.state = state;
        this.updatedAt = LocalDateTime.now();
    }

}
