package com.peeerr.climbing.exception;

import com.peeerr.climbing.constant.ErrorMessage;

public class InvalidFileTypeException extends ClimbingException {

    public InvalidFileTypeException() {
        super(ErrorMessage.INVALID_FILE_TYPE);
    }

    @Override
    public int getStatusCode() {
        return 415;
    }

}
