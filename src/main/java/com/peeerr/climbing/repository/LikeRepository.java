package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Like;
import com.peeerr.climbing.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Long countLikeByPost(Post post);

    boolean existsLikeByMemberIdAndPostId(Long memberId, Long postId);

    Optional<Like> findLikeByMemberIdAndPostId(Long memberId, Long postId);

}
