package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ValidationErrorMessage.CATEGORY_NAME_NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryEditRequest {

    @Size(min = 1, max = 20)
    @NotBlank(message = CATEGORY_NAME_NOT_BLANK)
    private String categoryName;

    public static CategoryEditRequest of(String categoryName) {
        return new CategoryEditRequest(categoryName);
    }

}
