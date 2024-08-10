package com.peeerr.climbing.repository;

import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PopularPostResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.util.StringUtils;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.peeerr.climbing.domain.QCategory.category;
import static com.peeerr.climbing.domain.QLike.like;
import static com.peeerr.climbing.domain.QMember.member;
import static com.peeerr.climbing.domain.QPost.post;
import static com.querydsl.core.types.Projections.constructor;

public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @PersistenceContext
    private EntityManager entityManager;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostResponse> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        List<PostResponse> posts = queryFactory
                .select(constructor(PostResponse.class,
                        post.id,
                        post.title,
                        post.category.categoryName,
                        post.member.username,
                        post.createDate,
                        post.modifyDate,
                        Expressions.asNumber(
                                JPAExpressions.select(like.count())
                                        .from(like)
                                        .where(like.post.eq(post))
                        ).longValue().as("likeCount")
                ))
                .from(post)
                .join(post.category, category)
                .join(post.member, member)
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
        Post foundPost = queryFactory
                .selectFrom(post)
                .join(post.category, category).fetchJoin()
                .join(post.member, member).fetchJoin()
                .where(post.id.eq(postId))
                .fetchOne();

        return post != null ? Optional.of(foundPost) : Optional.empty();
    }

    @Override
    public List<PopularPostResponse> findPopularPosts() {
        String jpql = "SELECT new com.peeerr.climbing.dto.response.PopularPostResponse(p.id, p.title, c.categoryName, m.username, p.createDate, p.modifyDate, l.likeCount) " +
                "FROM Post p " +
                "JOIN p.category c " +
                "JOIN p.member m " +
                "JOIN (SELECT l.post.id AS postId, COUNT(l.id) AS likeCount FROM Like l GROUP BY l.post.id ORDER BY likeCount DESC LIMIT 20) l " +
                "ON l.postId = p.id";

        return entityManager.createQuery(jpql, PopularPostResponse.class)
                .getResultList();
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
