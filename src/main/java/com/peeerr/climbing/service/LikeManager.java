package com.peeerr.climbing.service;

import com.peeerr.climbing.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class LikeManager {

    private final PostRepository postRepository;
    private final LikeService likeService;

    private final String LOCK_PREFIX = "POST_";

    @Transactional
    public void like(Long memberId, Long postId) {
        try {
            postRepository.getNamedLock(LOCK_PREFIX + postId);
            likeService.likeWithNamedLock(memberId, postId);
        } finally {
            postRepository.releaseNamedLock(LOCK_PREFIX + postId);
        }
    }

}
