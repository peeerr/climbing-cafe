package com.peeerr.climbing.dto.post.request;

import com.peeerr.climbing.domain.post.Post;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    public static PostEditRequest of(String title, String content) {
        return new PostEditRequest(title, content);
    }

}
