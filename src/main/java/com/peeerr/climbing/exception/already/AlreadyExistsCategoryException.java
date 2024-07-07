package com.peeerr.climbing.exception.already;

import com.peeerr.climbing.constant.ErrorMessage;

public class AlreadyExistsCategoryException extends AlreadyExistsException {

    public AlreadyExistsCategoryException() {
        super(ErrorMessage.ALREADY_EXISTS_CATEGORY);
    }

}
