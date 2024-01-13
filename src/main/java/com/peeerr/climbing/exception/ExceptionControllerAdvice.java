package com.peeerr.climbing.exception;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

}
