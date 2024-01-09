package com.peeerr.climbing.exception.ex;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {

    private Map<String, String> errorMap;

    public ValidationException(String message, Map errorMap) {
        super(message);
        this.errorMap = errorMap;
    }

}
