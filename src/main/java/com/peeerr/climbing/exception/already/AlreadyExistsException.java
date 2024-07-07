package com.peeerr.climbing.exception.already;

import com.peeerr.climbing.exception.ClimbingException;

public class AlreadyExistsException extends ClimbingException {

    public AlreadyExistsException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 409;
    }

}
