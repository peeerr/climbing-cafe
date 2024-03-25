package com.peeerr.climbing.controller;

import com.peeerr.climbing.security.CustomUserDetails;
import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.exception.ValidationException;
import com.peeerr.climbing.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/files")
@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<ApiResponse> fileUrlListByPost(@PathVariable Long postId) {
        List<String> fileUrls = fileService.getFilesByPostId(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.success(fileUrls));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> fileUpload(@PathVariable Long postId,
                                                  @RequestParam List<MultipartFile> files,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (files == null) {
            throw new ValidationException(ErrorMessage.VALIDATION_ERROR, Map.of("files", ErrorMessage.NO_FILE_SELECTED));
        } else {
            for (MultipartFile file: files) {
                if (file.isEmpty()) {
                    throw new ValidationException(ErrorMessage.VALIDATION_ERROR, Map.of("files", ErrorMessage.NO_FILE_SELECTED));
                }
            }
        }

        Long loginId = userDetails.getMember().getId();
        fileService.uploadFiles(loginId, postId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse> fileUpdateDeleteFlag(@PathVariable String postId,
                                                            @PathVariable Long fileId,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long loginId = userDetails.getMember().getId();
        fileService.updateDeleteFlag(loginId, fileId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
