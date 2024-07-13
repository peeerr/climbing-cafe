package com.peeerr.climbing.repository;

import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.dto.response.QPostResponse;
import com.peeerr.climbing.domain.Post;
import com.peeerr.climbing.domain.QPost;
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

import static com.peeerr.climbing.domain.QCategory.category;
import static com.peeerr.climbing.domain.QMember.member;
import static com.peeerr.climbing.domain.QPost.post;

public class CustomPostRepositoryImpl implements CustomPostRepository {


    private final JPAQueryFactory queryFactory;

    public CustomPostRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<PostResponse> findPostsFilteredByCategoryIdAndSearchWord(Long categoryId, PostSearchCondition condition, Pageable pageable) {
        List<PostResponse> posts = queryFactory
                .select(new QPostResponse(
                        post.id,
                        post.category.categoryName,
                        post.member.username,
                        post.createDate,
                        post.modifyDate))
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
        Post post = queryFactory
                .selectFrom(QPost.post)
                .join(QPost.post.category, category).fetchJoin()
                .join(QPost.post.member, member).fetchJoin()
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        return post != null ? Optional.of(post) : Optional.empty();
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
