package com.peeerr.climbing.exception.notfound;

import com.peeerr.climbing.constant.ErrorMessage;

public class CategoryNotFoundException extends NotFoundException {

    public CategoryNotFoundException() {
        super(ErrorMessage.CATEGORY_NOT_FOUND);
    }

}
