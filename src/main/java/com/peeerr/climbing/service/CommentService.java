package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Comment;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.CommentCreateRequest;
import com.peeerr.climbing.dto.request.CommentEditRequest;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.CommentRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public void addComment(Long postId, CommentCreateRequest request, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));

        Optional.ofNullable(request.getParentId())
                .map(parentId -> commentRepository.findById(parentId)
                        .orElseThrow(() -> new ClimbingException(ErrorCode.COMMENT_NOT_FOUND)))
                .ifPresentOrElse(
                        parentComment -> commentRepository.save(request.toEntity(post, member, parentComment)),
                        () -> commentRepository.save(request.toEntity(post, member, null))
                );
    }

    public void editComment(Long commentId, CommentEditRequest request, Long loginId) {
        Comment comment = getCommentById(commentId);
        comment.checkOwner(loginId);

        comment.changeContent(request.getContent());
    }

    public void removeComment(Long commentId, Long loginId) {
        Comment comment = getCommentById(commentId);
        comment.checkOwner(loginId);

        commentRepository.delete(comment);
    }

}
