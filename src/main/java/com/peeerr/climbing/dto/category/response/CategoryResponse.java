package com.peeerr.climbing.dto.category.response;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.post.Post;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class CategoryResponse {

    private String categoryName;
    private List<Post> posts;

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getCategoryName(), category.getPosts());
    }

}
