package com.peeerr.climbing.dto.post.response;

import com.peeerr.climbing.domain.post.Post;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponse {

    private Long postId;
    private String categoryName;
    private String writer;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static PostResponse from(Post post) {
        return new PostResponse(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreateDate(),
                post.getModifyDate());
    }

    @QueryProjection
    public PostResponse(Long postId, String categoryName, String writer, LocalDateTime createDate, LocalDateTime modifyDate) {
        this.postId = postId;
        this.categoryName = categoryName;
        this.writer = writer;
        this.createDate = createDate;
        this.modifyDate = modifyDate;
    }

}
