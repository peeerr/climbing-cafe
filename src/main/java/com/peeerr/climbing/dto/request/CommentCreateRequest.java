package com.peeerr.climbing.dto.request;

import com.peeerr.climbing.domain.Comment;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.peeerr.climbing.exception.ValidationErrorMessage.CONTENT_NOT_BLANK;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentCreateRequest {

    private Long parentId;

    @Size(min = 1, max = 500)
    @NotBlank(message = CONTENT_NOT_BLANK)
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
