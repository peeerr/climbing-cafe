package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.dto.post.request.PostSearchCondition;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.peeerr.climbing.domain.category.QCategory.category;
import static com.peeerr.climbing.domain.post.QPost.post;
import static com.peeerr.climbing.domain.user.QMember.member;

public class CustomPostRepositoryImpl implements CustomPostRepository {


    private final JPAQueryFactory queryFactory;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        List<Post> posts = queryFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(categoryEq(categoryId), titleContains(condition.getTitle()), contentContains(condition.getContent()))
                .orderBy(post.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(categoryEq(categoryId), titleContains(condition.getTitle()), contentContains(condition.getContent()));

        return PageableExecutionUtils.getPage(posts, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Optional<Post> findPostById(Long postId) {
        return Optional.of(queryFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne());
    }

    public BooleanExpression categoryEq(Long categoryId) {
        return !Objects.isNull(categoryId) ? post.category.id.eq(categoryId) : null;
    }

    public BooleanExpression titleContains(String title) {
        return !StringUtils.isNullOrEmpty(title) ? post.title.contains(title) : null;
    }

    public BooleanExpression contentContains(String content) {
        return !StringUtils.isNullOrEmpty(content) ? post.content.contains(content) : null;
    }

}
