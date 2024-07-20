package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ErrorMessage.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostEditRequest {

    @Size(min = 1, max = 100)
    @NotBlank(message = TITLE_NOT_BLANK)
    private String title;

    @NotBlank(message = CONTENT_NOT_BLANK)
    private String content;

    @NotNull(message = CATEGORY_NOT_NULL)
    private Long categoryId;

    public static PostEditRequest of(String title, String content, Long categoryId) {
        return new PostEditRequest(title, content, categoryId);
    }

}
