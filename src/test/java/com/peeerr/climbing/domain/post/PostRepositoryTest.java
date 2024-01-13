package com.peeerr.climbing.domain.post;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
class PostRepositoryTest {

    @Autowired private PostRepository postRepository;
    @Autowired private CategoryRepository categoryRepository;

    @DisplayName("게시물 하나를 저장한다.")
    @Test
    void save() throws Exception {
        //given
        Post post = createPost();

        //when
        Post savedPost = postRepository.save(post);

        //then
        assertThat(savedPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(savedPost.getContent()).isEqualTo(post.getContent());
        assertThat(savedPost.getCategory().getId()).isEqualTo(post.getCategory().getId());
    }

    @DisplayName("전체 게시물을 페이징해서 조회해 온다.")
    @Test
    void findAll() throws Exception {
        //given
        Category category = categoryRepository.save(Category.of("자유 게시판"));

        Post post1 = Post.builder()
                .title("제목 테스트1")
                .content("본문 테스트1")
                .category(category)
                .build();
        Post post2 = Post.builder()
                .title("제목 테스트2")
                .content("본문 테스트2")
                .category(category)
                .build();
        Post post3 = Post.builder()
                .title("제목 테스트3")
                .content("본문 테스트3")
                .category(category)
                .build();

        Pageable pageable = PageRequest.of(0, 2, Sort.Direction.DESC, "id");

        postRepository.saveAll(List.of(post1, post2, post3));

        //when
        Page<Post> posts = postRepository.findAll(pageable);

        //then
        assertThat(posts)
                .hasSize(2)
                .extracting("id", "title", "content", "category")
                .containsExactlyInAnyOrder(
                        tuple(3L, "제목 테스트3", "본문 테스트3", category),
                        tuple(2L, "제목 테스트2", "본문 테스트2", category)
                );
    }

    @DisplayName("id로 게시물을 조회해 온다.")
    @Test
    void findById() throws Exception {
        //given
        Post post = createPost();

        Post savedPost = postRepository.save(post);
        Long postId = savedPost.getId();

        //when
        Post foundPost = postRepository.findById(postId).get();

        //then
        assertThat(foundPost.getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(foundPost.getContent()).isEqualTo(savedPost.getContent());
        assertThat(foundPost.getCategory().getId()).isEqualTo(savedPost.getCategory().getId());
    }

    @DisplayName("id에 해당하는 게시물을 삭제한다.")
    @Test
    void deleteById() throws Exception {
        //given
        Post post = createPost();

        Post savedPost = postRepository.save(post);
        Long postId = savedPost.getId();

        long count = postRepository.count();

        //when
        postRepository.deleteById(postId);

        //then
        assertThat(postRepository.count()).isEqualTo(count - 1);
    }

    private Post createPost() {
        Category category = categoryRepository.save(Category.of("자유 게시판"));
        Post post = Post.builder()
                .title("제목 테스트")
                .content("본문 테스트")
                .category(category)
                .build();

        return post;
    }

}
