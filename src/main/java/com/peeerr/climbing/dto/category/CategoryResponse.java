package com.peeerr.climbing.dto.category;

import com.peeerr.climbing.domain.category.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryResponse {

    private String categoryName;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getCategoryName());
    }

}
