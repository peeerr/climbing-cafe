package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class PostNotFoundException extends NotFoundException {

    public PostNotFoundException() {
        super(ErrorMessage.POST_NOT_FOUND);
    }

}
