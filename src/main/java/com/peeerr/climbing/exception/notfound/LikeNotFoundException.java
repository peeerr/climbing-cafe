package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class LikeNotFoundException extends NotFoundException {

    public LikeNotFoundException() {
        super(ErrorMessage.LIKE_NOT_FOUND);
    }

}
