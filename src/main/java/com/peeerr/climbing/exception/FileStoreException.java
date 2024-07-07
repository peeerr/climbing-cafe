package com.peeerr.climbing.exception;

import com.peeerr.climbing.constant.ErrorMessage;

public class FileStoreException extends ClimbingException {

    public FileStoreException() {
        super(ErrorMessage.FILE_STORE_FAILED);
    }

    @Override
    public int getStatusCode() {
        return 500;
    }

}
