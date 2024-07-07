package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.exception.ClimbingException;

public class NotFoundException extends ClimbingException {

    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }

}
