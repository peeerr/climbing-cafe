package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.dto.post.request.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Objects;

import static com.peeerr.climbing.domain.post.QPost.post;

public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<Post> getPostsFilteredByBoardIdAndSearchWord(Long boardId, PostSearchCondition condition) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(boardEq(boardId), titleContains(condition.getTitle()), contentContains(condition.getContent()))
                .fetch();

        return posts;
    }

    public BooleanExpression boardEq(Long boardId) {
        return !Objects.isNull(boardId) ? post.category.id.eq(boardId) : null;
    }

    public BooleanExpression titleContains(String title) {
        return !StringUtils.isNullOrEmpty(title) ? post.title.contains(title) : null;
    }

    public BooleanExpression contentContains(String content) {
        return !StringUtils.isNullOrEmpty(content) ? post.content.contains(content) : null;
    }

}
