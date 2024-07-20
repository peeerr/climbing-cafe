package com.peeerr.climbing.dto.request;

import com.peeerr.climbing.validation.annotation.NotDuplicateCategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ErrorMessage.ALREADY_EXISTS_CATEGORY;
import static com.peeerr.climbing.exception.ErrorMessage.CATEGORY_NAME_NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryCreateRequest {

    @Size(min = 1, max = 20)
    @NotBlank(message = CATEGORY_NAME_NOT_BLANK)
    @NotDuplicateCategoryName(message = ALREADY_EXISTS_CATEGORY)
    private String categoryName;

    public static CategoryCreateRequest of(String categoryName) {
        return new CategoryCreateRequest(categoryName);
    }

}
