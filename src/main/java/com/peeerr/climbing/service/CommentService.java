package com.peeerr.climbing.service;

import com.peeerr.climbing.dto.request.CommentCreateRequest;
import com.peeerr.climbing.dto.request.CommentEditRequest;
import com.peeerr.climbing.entity.Comment;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.entity.Post;
import com.peeerr.climbing.exception.AccessDeniedException;
import com.peeerr.climbing.exception.notfound.CommentNotFoundException;
import com.peeerr.climbing.exception.notfound.PostNotFoundException;
import com.peeerr.climbing.repository.CommentRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Transactional
    public void addComment(Long postId, CommentCreateRequest request, Member member) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        Optional.ofNullable(request.getParentId())
                .map(parentId -> commentRepository.findById(parentId)
                        .orElseThrow(CommentNotFoundException::new))
                .ifPresentOrElse(
                        parentComment -> commentRepository.save(request.toEntity(post, member, parentComment)),
                        () -> commentRepository.save(request.toEntity(post, member, null))
                );
    }

    @Transactional
    public void editComment(Long commentId, CommentEditRequest request, Long loginId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(loginId)) {
            throw new AccessDeniedException();
        }

        comment.changeContent(request.getContent());
    }

    @Transactional
    public void removeComment(Long commentId, Long loginId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getMember().getId().equals(loginId)) {
            throw new AccessDeniedException();
        }

        commentRepository.delete(comment);
    }

}
