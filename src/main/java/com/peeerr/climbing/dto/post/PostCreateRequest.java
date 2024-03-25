package com.peeerr.climbing.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostCreateRequest {

    @Size(min = 1, max = 100)
    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "본문을 입력해 주세요.")
    private String content;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;

    public static PostCreateRequest of(String title, String content, Long categoryId) {
        return new PostCreateRequest(title, content, categoryId);
    }

}
