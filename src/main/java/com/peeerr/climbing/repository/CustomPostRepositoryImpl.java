package com.peeerr.climbing.repository;

import static com.peeerr.climbing.domain.QCategory.category;
import static com.peeerr.climbing.domain.QMember.member;
import static com.peeerr.climbing.domain.QPost.post;
import static com.querydsl.core.types.Projections.constructor;

import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PopularPostResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostResponse> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition,
                                                                         Pageable pageable) {
        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .where(
                        categoryEq(categoryId),
                        contains(post.title, post.content, condition)
                )
                .orderBy(post.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<PostResponse> posts = queryFactory
                .select(constructor(PostResponse.class,
                        post.id,
                        post.title,
                        post.category.categoryName,
                        post.member.username,
                        post.createDate,
                        post.modifyDate,
                        post.likeCount
                ))
                .from(post)
                .join(post.category, category)
                .join(post.member, member)
                .where(post.id.in(ids))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(categoryEq(categoryId), titleContains(condition.getTitle()),
                        contentContains(condition.getContent()));

        return PageableExecutionUtils.getPage(posts, pageable, () -> countQuery.fetchOne());
    }

    @Override
    public Optional<Post> findPostById(Long postId) {
        Post foundPost = queryFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne();

        return Optional.ofNullable(foundPost);
    }

    @Override
    public List<PopularPostResponse> findPopularPosts() {
        return queryFactory
                .select(Projections.constructor(PopularPostResponse.class,
                        post.id,
                        post.title,
                        category.categoryName,
                        member.username,
                        post.createDate,
                        post.modifyDate,
                        post.likeCount
                ))
                .from(post)
                .join(post.category, category)
                .join(post.member, member)
                .orderBy(post.likeCount.desc())
                .limit(20)
                .fetch();
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

    private BooleanExpression contains(StringPath title, StringPath content, PostSearchCondition condition) {
        return Stream.of(
                        condition.getTitle() != null
                                ? Expressions.numberTemplate(Double.class,
                                        "function('match_against', {0}, {1})",
                                        title, condition.getTitle())
                                .gt(0)
                                : null,
                        condition.getContent() != null
                                ? Expressions.numberTemplate(Double.class,
                                        "function('match_against', {0}, {1})",
                                        content, condition.getContent())
                                .gt(0)
                                : null
                )
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .orElse(null);
    }

}
