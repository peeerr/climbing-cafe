package com.peeerr.climbing.exception.already;

import com.peeerr.climbing.constant.ErrorMessage;

public class AlreadyExistsUsernameException extends AlreadyExistsException {

    public AlreadyExistsUsernameException() {
        super(ErrorMessage.ALREADY_EXISTS_USERNAME);
    }

}
