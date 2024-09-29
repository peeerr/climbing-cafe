package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.FileUploadState;
import com.peeerr.climbing.domain.FileUploadStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FileUploadStatusRepository extends JpaRepository<FileUploadStatus, String> {

    @Modifying
    @Query("UPDATE FileUploadStatus f SET f.state = :state, f.updatedAt = CURRENT_TIMESTAMP WHERE f.fileId = :fileId")
    void updateStatus(String fileId, FileUploadState state);

}
