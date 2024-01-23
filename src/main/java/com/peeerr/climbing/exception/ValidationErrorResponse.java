package com.peeerr.climbing.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ValidationErrorResponse {

    private String message;
    private Map<String, String> errorMap;

    public static ValidationErrorResponse of(String message, Map<String, String> errorMap) {
        return new ValidationErrorResponse(message, errorMap);
    }

}
