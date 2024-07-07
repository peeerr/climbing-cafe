package com.peeerr.climbing.advice;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.dto.ErrorResponse;
import com.peeerr.climbing.exception.ClimbingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ClimbingException.class)
    public ResponseEntity<ErrorResponse> validation(ClimbingException e) {
        int statusCode = e.getStatusCode();

        ErrorResponse response = ErrorResponse.builder()
            .code(statusCode)
            .message(e.getMessage())
            .validation(e.getValidation())
            .build();

        return ResponseEntity.status(statusCode)
            .body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> fileMaxSize() {
        ErrorResponse response = ErrorResponse.builder()
            .code(HttpStatus.PAYLOAD_TOO_LARGE.value())
            .message(ErrorMessage.FILE_SIZE_EXCEEDED)
            .build();

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exception(Exception e) {
        ErrorResponse response = ErrorResponse.builder()
            .code(500)
            .message(e.getMessage())
            .build();

        return ResponseEntity.status(500)
            .body(response);
    }

}
