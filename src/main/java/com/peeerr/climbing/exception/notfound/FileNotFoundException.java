package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class FileNotFoundException extends NotFoundException {

    public FileNotFoundException() {
        super(ErrorMessage.FILE_NOT_FOUND);
    }

}
