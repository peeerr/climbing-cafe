package com.peeerr.climbing.advice;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.advice.exception.*;
import com.peeerr.climbing.exception.*;
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
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> validation(ValidationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(e.getMessage(), e.getErrorMap()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> fileMaxSize(MaxUploadSizeExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiResponse.of(ErrorMessage.FILE_SIZE_EXCEEDED));
    }

    @ExceptionHandler(DirectoryCreateException.class)
    public ResponseEntity<?> createDirectory(DirectoryCreateException e) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(FileStoreException.class)
    public ResponseEntity<ApiResponse> storeFile(FileStoreException e) {
        return ResponseEntity.internalServerError()
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(FileAlreadyDeletedException.class)
    public ResponseEntity<ApiResponse> fileAlreadyDeleted(FileAlreadyDeletedException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(DuplicationException.class)
    public ResponseEntity<ApiResponse> duplicated(DuplicationException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse> unAuthorizedAccess(UnauthorizedAccessException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ApiResponse> alreadyExists(AlreadyExistsException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(e.getMessage()));
    }

    @ExceptionHandler(FileTypeException.class)
    public ResponseEntity<ApiResponse> fileType(FileTypeException e) {
        return ResponseEntity.badRequest()
                .body(ApiResponse.of(e.getMessage()));
    }

}
