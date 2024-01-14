package com.peeerr.climbing.dto.category.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryCreateRequest {

    @Size(min = 1, max = 20)
    @NotBlank(message = "카테고리명을 입력해 주세요.")
    private String categoryName;

    public static CategoryCreateRequest of(String categoryName) {
        return new CategoryCreateRequest(categoryName);
    }

}
