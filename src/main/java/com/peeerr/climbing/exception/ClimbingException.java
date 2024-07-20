package com.peeerr.climbing.exception;

import lombok.Getter;

@Getter
public class ClimbingException extends RuntimeException {

    private final ErrorMessage errorMessage;

    public ClimbingException(ErrorMessage errorMessage) {
        super(errorMessage.getMessage());
        this.errorMessage = errorMessage;
    }

}
