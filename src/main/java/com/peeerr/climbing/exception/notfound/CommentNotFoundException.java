package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class CommentNotFoundException extends NotFoundException {

    public CommentNotFoundException() {
        super(ErrorMessage.COMMENT_NOT_FOUND);
    }

}
