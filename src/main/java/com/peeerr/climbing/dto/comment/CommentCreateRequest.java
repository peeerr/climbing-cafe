package com.peeerr.climbing.dto.comment;

import com.peeerr.climbing.domain.comment.Comment;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.user.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentCreateRequest {

    private Long parentId;

    @Size(min = 1, max = 500)
    @NotBlank(message = "내용을 입력해 주세요.")
    private String content;

    public Comment toEntity(Post post, Member member, Comment parentComment) {
        return Comment.builder()
                .post(post)
                .member(member)
                .parentComment(parentComment)
                .content(this.content)
                .build();
    }

    public static CommentCreateRequest of(Long parentId, String content) {
        return new CommentCreateRequest(parentId, content);
    }

}
