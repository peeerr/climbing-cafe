package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.dto.post.request.PostSearchCondition;

import java.util.List;

public interface CustomPostRepository {

    List<Post> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition);

}
