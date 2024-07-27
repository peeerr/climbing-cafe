package com.peeerr.climbing.dto.response;

import com.peeerr.climbing.domain.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CommentResponse {

    private Long id;
    private String content;
    private String username;
    private Long parentId;

    private List<CommentResponse> childComments;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static CommentResponse from(Comment comment) {
        Comparator<CommentResponse> childCommentComparator = Comparator
                .comparing(CommentResponse::getCreateDate)
                .thenComparingLong(CommentResponse::getId);

        List<CommentResponse> childComments = new ArrayList<>();
        childComments.stream()
                .sorted(childCommentComparator)
                .toList();

        return new CommentResponse(comment.getId(),
                comment.getContent(),
                comment.getMember().getUsername(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                childComments,
                comment.getCreateDate(),
                comment.getModifyDate());
    }

    public boolean hasParent() {
        return parentId != null;
    }

}
