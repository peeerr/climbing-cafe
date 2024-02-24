package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.like.Like;
import com.peeerr.climbing.domain.like.LikeRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.domain.user.MemberRepository;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.AlreadyExistsException;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Long getLikeCount(Long postId) {
        return likeRepository.countLikeByPostId(postId);
    }

    @Transactional
    public void like(Long loginId, Long postId) {
        if (likeRepository.existsLikeByMemberIdAndPostId(loginId, postId)) {
            throw new AlreadyExistsException(ErrorMessage.LIKE_ALREADY_EXISTS);
        }

        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));
        Member member = memberRepository.findById(loginId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.MEMBER_NOT_FOUND));

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void unlike(Long loginId, Long postId) {
        Like like = likeRepository.findLikeByMemberIdAndPostId(loginId, postId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }

}
