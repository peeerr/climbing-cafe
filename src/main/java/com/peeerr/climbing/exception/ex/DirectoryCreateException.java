package com.peeerr.climbing.exception.ex;

import lombok.Getter;

@Getter
public class DirectoryCreateException extends RuntimeException {

    public DirectoryCreateException(String message) {
        super(message);
    }

}
