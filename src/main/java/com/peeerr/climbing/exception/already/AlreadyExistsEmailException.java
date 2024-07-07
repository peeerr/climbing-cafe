package com.peeerr.climbing.exception.already;

import com.peeerr.climbing.constant.ErrorMessage;

public class AlreadyExistsEmailException extends AlreadyExistsException {

    public AlreadyExistsEmailException() {
        super(ErrorMessage.ALREADY_EXISTS_EMAIL);
    }

}
