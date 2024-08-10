package com.peeerr.climbing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class PostResponse {

    private Long postId;
    private String title;
    private String categoryName;
    private String writer;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    private Long likeCount;

}
