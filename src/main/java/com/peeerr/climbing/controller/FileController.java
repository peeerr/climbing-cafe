package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.constant.MessageConstant;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.file.request.FileUploadRequest;
import com.peeerr.climbing.exception.ex.ValidationException;
import com.peeerr.climbing.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<ApiResponse> fileUpload(@ModelAttribute @Valid FileUploadRequest fileUploadRequest,
                                                  BindingResult bindingResult) {
        if (fileUploadRequest.getFiles() == null) {
            throw new ValidationException(MessageConstant.VALIDATION_ERROR, Map.of("files", MessageConstant.NO_FILE_SELECTED));
        } else {
            for (MultipartFile file: fileUploadRequest.getFiles()) {
                if (file.isEmpty()) {
                    throw new ValidationException(MessageConstant.VALIDATION_ERROR, Map.of("files", MessageConstant.NO_FILE_SELECTED));
                }
            }
        }

        List<String> uploadedFilesAllPaths = fileService.uploadFiles(fileUploadRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(uploadedFilesAllPaths));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<ApiResponse> fileUpdateDeleteFlag(@PathVariable Long fileId) {
        fileService.updateDeleteFlag(fileId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
