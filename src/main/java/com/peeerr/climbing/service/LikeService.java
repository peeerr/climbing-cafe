package com.peeerr.climbing.service;

import com.peeerr.climbing.entity.Like;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.entity.Post;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.repository.MemberRepository;
import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.exception.AlreadyExistsException;
import com.peeerr.climbing.exception.EntityNotFoundException;
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
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.POST_NOT_FOUND));

        return likeRepository.countLikeByPost(post);
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
