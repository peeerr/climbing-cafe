package com.peeerr.climbing.dto.common;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ApiResponse<T> {

    private final T data;

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse(data);
    }

}
