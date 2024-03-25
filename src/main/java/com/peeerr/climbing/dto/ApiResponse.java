package com.peeerr.climbing.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {

    private String message;
    private T data;

    public static <T> ApiResponse<T> of(String message, T data) {
        return new ApiResponse(message, data);
    }

    public static <T> ApiResponse<T> of(String message) {
        return new ApiResponse(message, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse("success", data);
    }

    public static ApiResponse success() {
        return new ApiResponse("success", null);
    }

}
