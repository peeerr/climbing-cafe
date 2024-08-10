package com.peeerr.climbing.dto.response;

import com.peeerr.climbing.domain.File;
import com.peeerr.climbing.domain.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostDetailResponse {

    private Long postId;
    private String title;
    private String content;
    private String categoryName;
    private String writer;
    private List<String> filePaths;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    private Long likeCount;

    public static PostDetailResponse of(Post post, Long likeCount) {
        return new PostDetailResponse(post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory().getCategoryName(),
                post.getMember().getUsername(),
                post.getFiles().stream()
                        .map(File::getFilePath)
                        .toList(),
                post.getCreateDate(),
                post.getModifyDate(),
                likeCount);
    }

}
