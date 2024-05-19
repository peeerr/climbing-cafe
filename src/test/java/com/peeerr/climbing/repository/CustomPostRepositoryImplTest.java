package com.peeerr.climbing.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.peeerr.climbing.dto.post.PostResponse;
import com.peeerr.climbing.dto.post.PostSearchCondition;
import com.peeerr.climbing.entity.Post;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@DataJpaTest
public class CustomPostRepositoryImplTest {

    @Autowired private CustomPostRepositoryImpl customPostRepository;
    @Autowired private PostRepository postRepository;

    @BeforeEach
    public void cleanup() {
        postRepository.deleteAll();
    }

    @DisplayName("조건에 맞는 게시물을 모두 조회한다. (필터: 검색어와 카테고리)")
    @Test
    public void findPostsFilteredByCategoryIdAndSearchWord() {
        //given
        Long categoryId = 1L;
        PostSearchCondition condition = PostSearchCondition.of("title", "content");
        Pageable pageable = PageRequest.of(0, 10);

        //when
        Page<PostResponse> result = postRepository.findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(0);
    }

    @DisplayName("카테고리가 주어지지 않으면 null 을 반환한다.")
    @Test
    public void categoryEq() {
        //given
        Long categoryId = null;

        //when
        BooleanExpression expression = customPostRepository.categoryEq(categoryId);

        //then
        assertThat(expression).isNull();
    }

    @DisplayName("제목이 주어지지 않으면 null 을 반환한다.")
    @Test
    public void titleContainsTest() {
        //given
        String title = "";

        //when
        BooleanExpression expression = customPostRepository.titleContains(title);

        //then
        assertThat(expression).isNull();
    }

    @DisplayName("본문이 주어지지 않으면 null 을 반환한다.")
    @Test
    public void contentContainsTest() {
        //given
        String content = "";

        //when
        BooleanExpression expression = customPostRepository.contentContains(content);

        //then
        assertThat(expression).isNull();
    }

    @DisplayName("게시물 ID에 해당하는 게시물을 상세조회한다.")
    @Test
    public void findPostById() {
        //given
        Long postId = 1L;

        //when
        Optional<Post> result = postRepository.findPostById(postId);

        //then
        assertThat(result.isPresent()).isFalse();
    }

}
