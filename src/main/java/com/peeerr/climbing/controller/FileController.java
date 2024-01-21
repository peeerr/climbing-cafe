package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.dto.file.request.FileUploadRequest;
import com.peeerr.climbing.exception.ex.ValidationException;
import com.peeerr.climbing.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<?> fileUpload(@ModelAttribute @Valid FileUploadRequest fileUploadRequest,
                                        BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException("유효성 검사 오류", errorMap);
        }

        for (MultipartFile file: fileUploadRequest.getFiles()) {
            if (file.isEmpty()) {
                throw new ValidationException("유효성 검사 오류", Map.of("files", "파일을 첨부해야 합니다."));
            }
        }

        fileService.uploadFiles(fileUploadRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("success", "파일 업로드 성공", null));
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<?> fileUpdateDeleteFlag(@PathVariable Long fileId) {
        fileService.updateDeleteFlag(fileId);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "파일 삭제 성공", null));
    }

}
