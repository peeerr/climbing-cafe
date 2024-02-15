package com.peeerr.climbing.dto.post.response;

import com.peeerr.climbing.domain.comment.Comment;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.dto.comment.response.CommentResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PostWithCommentsResponse {

    private String title;
    private String content;
    private String username;
    private String categoryName;

    private List<CommentResponse> comments;

    private LocalDateTime createDate;
    private LocalDateTime modifyDate;

    public static PostWithCommentsResponse from(Post post) {
        return new PostWithCommentsResponse(post.getTitle(),
                post.getContent(),
                post.getMember().getUsername(),
                post.getCategory().getCategoryName(),
                organizeComments(post.getComments()),
                post.getCreateDate(),
                post.getModifyDate());
    }

    private static List<CommentResponse> organizeComments(List<Comment> comments) {
        Map<Long, CommentResponse> map = comments.stream()
                .map(CommentResponse::from)
                .collect(Collectors.toMap(CommentResponse::getId, Function.identity()));

        map.values().stream()
                .filter(CommentResponse::hasParent)
                .forEach(comment -> {
                    CommentResponse parentComment = map.get(comment.getParentId());
                    parentComment.getChildComments().add(comment);
                });

        return map.values().stream()
                .filter(comment -> !comment.hasParent())
                .sorted(Comparator.comparing(CommentResponse::getCreateDate)
                        .thenComparingLong(CommentResponse::getId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
