package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.ValidationException;
import com.peeerr.climbing.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/files")
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse> fileUpload(@PathVariable Long postId,
                                                  @RequestParam List<MultipartFile> files) {
        if (files == null) {
            throw new ValidationException(ErrorMessage.VALIDATION_ERROR, Map.of("files", ErrorMessage.NO_FILE_SELECTED));
        } else {
            for (MultipartFile file: files) {
                if (file.isEmpty()) {
                    throw new ValidationException(ErrorMessage.VALIDATION_ERROR, Map.of("files", ErrorMessage.NO_FILE_SELECTED));
                }
            }
        }

        List<String> uploadedFilesAllPaths = fileService.uploadFiles(postId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(uploadedFilesAllPaths));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse> fileUpdateDeleteFlag(@PathVariable String postId,
                                                            @PathVariable Long fileId) {
        fileService.updateDeleteFlag(fileId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
