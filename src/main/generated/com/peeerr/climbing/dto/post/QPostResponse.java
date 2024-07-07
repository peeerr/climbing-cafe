package com.peeerr.climbing.dto.post;

import com.peeerr.climbing.dto.response.PostResponse;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * com.peeerr.climbing.dto.post.QPostResponse is a Querydsl Projection type for PostResponse
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPostResponse extends ConstructorExpression<PostResponse> {

    private static final long serialVersionUID = -923548853L;

    public QPostResponse(com.querydsl.core.types.Expression<Long> postId, com.querydsl.core.types.Expression<String> categoryName, com.querydsl.core.types.Expression<String> writer, com.querydsl.core.types.Expression<java.time.LocalDateTime> createDate, com.querydsl.core.types.Expression<java.time.LocalDateTime> modifyDate) {
        super(PostResponse.class, new Class<?>[]{long.class, String.class, String.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, postId, categoryName, writer, createDate, modifyDate);
    }

}

