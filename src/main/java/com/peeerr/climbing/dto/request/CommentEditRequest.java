package com.peeerr.climbing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentEditRequest {

    @Size(min = 1, max = 500)
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    public static CommentEditRequest of(String content) {
        return new CommentEditRequest(content);
    }

}
