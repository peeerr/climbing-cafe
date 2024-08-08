package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PopularPostResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomPostRepository {

    Page<PostResponse> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable);

    Optional<Post> findPostById(Long postId);

    List<PopularPostResponse> findPopularPosts();

}
