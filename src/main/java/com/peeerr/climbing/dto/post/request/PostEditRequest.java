package com.peeerr.climbing.dto.post.request;

import com.peeerr.climbing.domain.post.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class PostEditRequest {

    @Size(min = 1, max = 100)
    @NotBlank(message = "제목을 입력해 주세요.")
    private String title;

    @NotBlank(message = "본문을 입력해 주세요")
    private String content;

    @NotNull(message = "카테고리를 선택해 주세요.")
    private Long categoryId;

    public static PostEditRequest of(String title, String content, Long categoryId) {
        return new PostEditRequest(title, content, categoryId);
    }

}
