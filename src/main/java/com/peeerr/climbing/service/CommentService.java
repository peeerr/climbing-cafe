package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.comment.Comment;
import com.peeerr.climbing.domain.comment.CommentRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.comment.request.CommentCreateRequest;
import com.peeerr.climbing.dto.comment.request.CommentEditRequest;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void addComment(Long postId, CommentCreateRequest request, Member member) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        if (request.getParentId() == null) {
            commentRepository.save(request.toEntity(post, member, null));
        } else {
            Comment parentComment = commentRepository.findById(request.getParentId()).orElseThrow(
                    () -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

            commentRepository.save(request.toEntity(post, member, parentComment));
        }
    }

    @Transactional
    public void editComment(Long commentId, CommentEditRequest request, Long loginId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        comment.changeContent(request.getContent());
    }

    @Transactional
    public void removeComment(Long commentId, Long loginId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.COMMENT_NOT_FOUND));

        if (!comment.getMember().getId().equals(loginId)) {
            throw new UnauthorizedAccessException(ErrorMessage.NO_ACCESS_PERMISSION);
        }

        commentRepository.delete(comment);
    }

}
