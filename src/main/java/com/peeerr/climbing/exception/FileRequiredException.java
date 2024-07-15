package com.peeerr.climbing.exception;

import com.peeerr.climbing.constant.ErrorMessage;

public class FileRequiredException extends ClimbingException {

    public FileRequiredException() {
        super(ErrorMessage.FILE_REQUIRED);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
