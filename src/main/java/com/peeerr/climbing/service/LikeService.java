package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.service.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final LikeValidator likeValidator;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));
    }

    public void like(Long memberId, Long postId) {
        likeValidator.validateLikeNotExists(memberId, postId);

        Post post = postRepository.findPostByIdWithPessimisticLock(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));

        Like like = Like.builder()
                .memberId(memberId)
                .postId(postId)
                .build();

        likeRepository.save(like);
        post.increaseLikeCount();
    }

    public void unlike(Long memberId, Long postId) {
        Like like = likeRepository.findLikeByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);

        Post post = getPostById(postId);
        post.decreaseLikeCount();
    }

}
