package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ValidationErrorMessage.CONTENT_NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentEditRequest {

    @Size(min = 1, max = 500)
    @NotBlank(message = CONTENT_NOT_BLANK)
    private String content;

    public static CommentEditRequest of(String content) {
        return new CommentEditRequest(content);
    }

}
