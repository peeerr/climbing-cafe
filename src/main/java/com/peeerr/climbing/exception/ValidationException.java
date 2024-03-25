package com.peeerr.climbing.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {

    private Map<String, String> errorMap;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Map errorMap) {
        super(message);
        this.errorMap = errorMap;
    }

}
