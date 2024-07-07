package com.peeerr.climbing.exception.already;

import com.peeerr.climbing.constant.ErrorMessage;

public class AlreadyExistsLikeException extends AlreadyExistsException {

    public AlreadyExistsLikeException() {
        super(ErrorMessage.ALREADY_EXISTS_LIKE);
    }

}
