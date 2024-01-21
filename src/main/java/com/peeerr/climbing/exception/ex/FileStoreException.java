package com.peeerr.climbing.exception.ex;

import lombok.Getter;

@Getter
public class FileStoreException extends RuntimeException {

    public FileStoreException(String message) {
        super(message);
    }

}
