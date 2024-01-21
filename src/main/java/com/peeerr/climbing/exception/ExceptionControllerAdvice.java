package com.peeerr.climbing.exception;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.exception.ex.DirectoryCreateException;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.FileStoreException;
import com.peeerr.climbing.exception.ex.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> entityNotFound(EntityNotFoundException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of("fail", e.getMessage(), null));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validation(ValidationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of("fail", e.getMessage(), e.getErrorMap()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileMaxSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.of("fail", "전체 파일 크기를 10MB 이하로 첨부해 주세요.", null));
    }

    @ExceptionHandler(DirectoryCreateException.class)
    public ResponseEntity<?> createDirectory(DirectoryCreateException e) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of("fail", e.getMessage(), null));
    }

    @ExceptionHandler(FileStoreException.class)
    public ResponseEntity<?> storeFile(FileStoreException e) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of("fail", e.getMessage(), null));
    }

}
