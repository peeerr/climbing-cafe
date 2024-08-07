package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.exception.ValidationErrorMessage;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.FileService;
import com.peeerr.climbing.validation.FileNotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/files")
@RestController
public class FileController {

    private final FileService fileService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> fileUrlListByPost(@PathVariable Long postId) {
        List<String> fileUrls = fileService.getFilesByPostId(postId);

        return ResponseEntity.ok()
                .body(ApiResponse.of(fileUrls));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> fileUpload(@PathVariable Long postId,
                                           @RequestParam @FileNotEmpty(message = ValidationErrorMessage.FILE_REQUIRED) List<MultipartFile> files,
                                           @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        fileService.uploadFiles(loginId, postId, files);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> fileUpdateDeleteFlag(@PathVariable String postId,
                                                     @PathVariable Long fileId,
                                                     @AuthenticationPrincipal MemberPrincipal userDetails) {
        Long loginId = userDetails.getMember().getId();
        fileService.updateDeleteFlag(loginId, fileId);

        return ResponseEntity.ok()
                .build();
    }

}
