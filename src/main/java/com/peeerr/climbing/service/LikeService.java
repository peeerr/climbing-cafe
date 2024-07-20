package com.peeerr.climbing.service;

import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Long getLikeCount(Long postId) {
        Post post = getPostById(postId);

        return likeRepository.countLikeByPost(post);
    }

    public void like(Long loginId, Long postId) {
        if (likeRepository.existsLikeByMemberIdAndPostId(loginId, postId)) {
            throw new ClimbingException(ErrorCode.ALREADY_EXISTS_LIKE);
        }

        Post post = getPostById(postId);
        Member member = memberRepository.findById(loginId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.MEMBER_NOT_FOUND));

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();

        likeRepository.save(like);
    }

    public void unlike(Long loginId, Long postId) {
        Like like = likeRepository.findLikeByMemberIdAndPostId(loginId, postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
    }

}
