package com.peeerr.climbing.dto.post.request;

import com.peeerr.climbing.domain.post.Post;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class PostCreateRequest {

    @Size(min = 1, max = 100)
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    public Post toEntity() {
        return Post.of(this.title, this.content);
    }

    public static PostCreateRequest of(String title, String content) {
        return new PostCreateRequest(title, content);
    }

}
