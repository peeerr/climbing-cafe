package com.peeerr.climbing.validator;

import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.peeerr.climbing.exception.ErrorCode.ALREADY_EXISTS_CATEGORY;

@RequiredArgsConstructor
@Component
public class CategoryValidator {

    private final CategoryRepository categoryRepository;

    public void validateCategoryNameUnique(String categoryName) {
        if (categoryRepository.existsByCategoryName(categoryName)) {
            throw new ClimbingException(ALREADY_EXISTS_CATEGORY);
        }
    }

}
