package com.peeerr.climbing.service;

import com.amazonaws.services.s3.model.PartETag;
import com.peeerr.climbing.constant.FileUploadState;
import com.peeerr.climbing.domain.File;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.FileChunkMessage;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.FileRepository;
import com.peeerr.climbing.repository.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FileStorageService {

    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final S3FileUploadService s3FileUploadService;
    private final FileUploadMessagingService messagingService;

    @Transactional
    public void completeUpload(FileChunkMessage message, String uploadId) {
        String fileId = message.getFileId();

        try {
            // 1. 모든 파트 정보 조회
            List<PartETag> partETags = messagingService.getPartETagsFromRedis(
                    fileId,
                    message.getTotalChunks()
            );

            // 2. S3 멀티파트 업로드 완료
            String fileUrl = s3FileUploadService.completeMultipartUpload(
                    message,
                    uploadId,
                    partETags
            );

            // 3. DB에 파일 정보 저장
            saveFileInfo(message, fileUrl);

            // 4. 리소스 정리 및 상태 업데이트
            messagingService.cleanupRedisKeys(fileId);
            messagingService.sendFileStatus(fileId, FileUploadState.COMPLETED);

            log.info("Successfully completed file upload. FileId: {}", fileId);

        } catch (Exception e) {
            log.error("Failed to complete upload. FileId: {}", fileId, e);
            handleCompletionFailure(message, uploadId);
            throw new ClimbingException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    private void saveFileInfo(FileChunkMessage message, String fileUrl) {
        try {
            Post post = postRepository.findById(message.getPostId())
                    .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));

            File fileEntity = File.builder()
                    .post(post)
                    .originalFilename(message.getOriginalFileName())
                    .filename(message.getS3FileName())
                    .filePath(fileUrl)
                    .build();

            fileRepository.save(fileEntity);

        } catch (Exception e) {
            log.error("Failed to save file info. FileId: {}", message.getFileId(), e);
            throw new ClimbingException(ErrorCode.FILE_STORE_FAILED);
        }
    }

    private void handleCompletionFailure(FileChunkMessage message, String uploadId) {
        String fileId = message.getFileId();
        try {
            // 1. S3 멀티파트 업로드 중단
            s3FileUploadService.abortMultipartUpload(message.getS3FileName(), uploadId);

            // 2. 상태 업데이트
            messagingService.sendFileStatus(fileId, FileUploadState.FAILED);
            messagingService.recordFailedFileId(fileId);

            // 3. Redis 키 정리
            messagingService.cleanupRedisKeys(fileId);

            log.info("Cleaned up resources after completion failure. FileId: {}", fileId);

        } catch (Exception e) {
            log.error("Failed to handle completion failure. FileId: {}", fileId, e);
        }
    }

}
