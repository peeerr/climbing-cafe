package com.peeerr.climbing.controller;

import com.peeerr.climbing.domain.FileUploadStatus;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.exception.ValidationErrorMessage;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.FileService;
import com.peeerr.climbing.service.FileUploadService;
import com.peeerr.climbing.validation.FileNotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class FileController {

    private final FileService fileService;
    private final FileUploadService fileUploadService;

    @GetMapping("/posts/{postId}/files")
    public ResponseEntity<ApiResponse<List<String>>> fileUrlListByPost(@PathVariable Long postId) {
        List<String> fileUrls = fileService.getFilesByPostId(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.of(fileUrls));
    }

    @PostMapping(value = "/posts/{postId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<List<String>>> fileUpload(@PathVariable Long postId,
                                                                @RequestParam @FileNotEmpty(message = ValidationErrorMessage.FILE_REQUIRED) List<MultipartFile> files,
                                                                @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();

        List<String> fileIds = fileUploadService.initiateFileUpload(loginId, postId, files);

        return ResponseEntity.accepted()
                .body(ApiResponse.of(fileIds));
    }

    @GetMapping("/files/{fileId}/status")
    public ResponseEntity<ApiResponse<FileUploadStatus>> getFileUploadStatus(@PathVariable String fileId) {
        FileUploadStatus status = fileUploadService.getFileUploadStatus(fileId);

        return ResponseEntity.ok()
                .body(ApiResponse.of(status));
    }

    @DeleteMapping("/posts/{postId}/files/{fileId}")
    public ResponseEntity<Void> fileUpdateDeleteFlag(@PathVariable String postId,
                                                     @PathVariable Long fileId,
                                                     @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        fileService.updateDeleteFlag(loginId, fileId);

        return ResponseEntity.ok()
                .build();
    }

}
