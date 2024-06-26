package com.peeerr.climbing.dto.category;

import com.peeerr.climbing.entity.Category;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryResponse {

    private Long categoryId;
    private String categoryName;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getCategoryName());
    }

}
