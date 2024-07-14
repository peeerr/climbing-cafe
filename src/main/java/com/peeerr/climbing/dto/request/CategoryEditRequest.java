package com.peeerr.climbing.dto.request;

import com.peeerr.climbing.validation.annotation.NotDuplicateCategoryName;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.constant.ErrorMessage.ALREADY_EXISTS_CATEGORY;
import static com.peeerr.climbing.constant.ErrorMessage.CATEGORY_NAME_NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryEditRequest {

    @Size(min = 1, max = 20)
    @NotBlank(message = CATEGORY_NAME_NOT_BLANK)
    @NotDuplicateCategoryName(message = ALREADY_EXISTS_CATEGORY)
    private String categoryName;

    public static CategoryEditRequest of(String categoryName) {
        return new CategoryEditRequest(categoryName);
    }

}
