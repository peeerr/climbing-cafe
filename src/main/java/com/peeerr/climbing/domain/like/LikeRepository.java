package com.peeerr.climbing.domain.like;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Long countLikeByPostId(Long postId);

    boolean existsLikeByMemberIdAndPostId(Long memberId, Long postId);

    Optional<Like> findLikeByMemberIdAndPostId(Long memberId, Long postId);

}
