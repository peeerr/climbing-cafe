package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.LikeRepository;
import com.peeerr.climbing.repository.PostRepository;
import com.peeerr.climbing.service.validator.LikeValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final LikeValidator likeValidator;
    private final RedisTemplate redisTemplate;
    private final String LIKE_COUNT_PREFIX = "post:likeCount:";

    public void like(Long memberId, Long postId) {
        likeValidator.validateLikeNotExists(memberId, postId);

        Like like = Like.builder()
                .memberId(memberId)
                .postId(postId)
                .build();

        likeRepository.save(like);
        redisTemplate.opsForValue().increment(LIKE_COUNT_PREFIX + postId, 1L);
    }

    public void unlike(Long memberId, Long postId) {
        Like like = likeRepository.findLikeByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.LIKE_NOT_FOUND));

        likeRepository.delete(like);
        redisTemplate.opsForValue().decrement(LIKE_COUNT_PREFIX + postId, 1L);
    }

    @Scheduled(cron = "0/5 * * * * ?")
    public void syncLikeCountToDB() {
        redisTemplate.keys(LIKE_COUNT_PREFIX + "*").forEach(key -> {
            Long postId = Long.parseLong(key.toString().replace(LIKE_COUNT_PREFIX, ""));
            Integer likeCount = (Integer) redisTemplate.opsForValue().get(key);

            updatePostLikeCount(postId, likeCount);

            redisTemplate.delete(key);
        });
    }

    private void updatePostLikeCount(Long postId, Integer likeCount) {
        postRepository.findPostById(postId)
                .ifPresent(post -> post.addLikeCount(likeCount));
    }

}
