package com.peeerr.climbing.exception;

import lombok.Getter;

@Getter
public class ClimbingException extends RuntimeException {

    private final ErrorCode errorCode;

    public ClimbingException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
