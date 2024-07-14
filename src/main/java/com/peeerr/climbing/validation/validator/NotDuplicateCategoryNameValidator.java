package com.peeerr.climbing.validation.validator;

import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.validation.annotation.NotDuplicateCategoryName;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotDuplicateCategoryNameValidator implements ConstraintValidator<NotDuplicateCategoryName, String> {

    private final CategoryRepository categoryRepository;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return !categoryRepository.existsByCategoryName(value);
    }

}
