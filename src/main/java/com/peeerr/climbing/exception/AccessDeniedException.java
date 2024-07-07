package com.peeerr.climbing.exception;

import com.peeerr.climbing.constant.ErrorMessage;

public class AccessDeniedException extends ClimbingException {

    public AccessDeniedException() {
        super(ErrorMessage.ACCESS_DENIED);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }

}
