package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.dto.post.request.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CustomPostRepository {

    Page<Post> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable);

    Optional<Post> findPostById(Long postId);

}
