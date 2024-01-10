package com.peeerr.climbing.dto.post.response;

import com.peeerr.climbing.domain.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostResponse {

    private String title;
    private String content;

    public static PostResponse from(Post post) {
        return new PostResponse(post.getTitle(), post.getContent());
    }

}
