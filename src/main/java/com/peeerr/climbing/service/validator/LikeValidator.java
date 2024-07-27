package com.peeerr.climbing.service.validator;

import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.peeerr.climbing.exception.ErrorCode.ALREADY_EXISTS_LIKE;

@RequiredArgsConstructor
@Component
public class LikeValidator {

    private final LikeRepository likeRepository;

    public void validateLikeNotExists(Long memberId, Long postId) {
        if (likeRepository.existsLikeByMemberIdAndPostId(memberId, postId)) {
            throw new ClimbingException(ALREADY_EXISTS_LIKE);
        }
    }

}
