package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.dto.post.request.PostSearchCondition;

import java.util.List;

public interface CustomPostRepository {

    List<Post> getPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition);

}
