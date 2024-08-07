package com.peeerr.climbing.exception;

import com.peeerr.climbing.dto.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalid(MethodArgumentNotValidException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code(400)
                .message(ValidationErrorMessage.VALIDATION_ERROR)
                .build();

        e.getFieldErrors().forEach(filedError ->
                response.addValidation(filedError.getField(), filedError.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> methodValidation(HandlerMethodValidationException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code(400)
                .message(ValidationErrorMessage.VALIDATION_ERROR)
                .build();

        e.getAllValidationResults().forEach(parameterResult ->
                parameterResult.getResolvableErrors().forEach(error ->
                        response.addValidation(parameterResult.getMethodParameter().getParameter().getName(), error.getDefaultMessage())
                )
        );

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> missingRequestPart(MissingServletRequestPartException e) {
        ErrorResponse response = ErrorResponse.builder()
                .code(400)
                .message(e.getMessage())
                .build();

        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(ClimbingException.class)
    public ResponseEntity<ErrorResponse> validation(ClimbingException e) {
        HttpStatus status = e.getErrorCode().getStatus();

        ErrorResponse response = ErrorResponse.builder()
                .code(status.value())
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(status)
                .body(response);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> fileMaxSize() {
        ErrorResponse response = ErrorResponse.builder()
                .code(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .message(ErrorCode.FILE_SIZE_EXCEEDED.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(response);
    }

}
