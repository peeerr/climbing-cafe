package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class MemberNotFoundException extends NotFoundException {

    public MemberNotFoundException() {
        super(ErrorMessage.MEMBER_NOT_FOUND);
    }

}
