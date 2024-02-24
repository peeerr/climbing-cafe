package com.peeerr.climbing.dto.post.response;

import com.peeerr.climbing.domain.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostResponse {

    private String title;
    private String content;
    private String categoryName;
    private String author;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static PostResponse from(Post post) {
        return new PostResponse(post.getTitle(),
                post.getContent(),
                post.getCategory().getCategoryName(),
                post.getMember().getUsername(),
                post.getCreateDate(),
                post.getModifyDate());
    }

}
