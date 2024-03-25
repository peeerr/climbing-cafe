package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.post.PostCreateRequest;
import com.peeerr.climbing.dto.post.PostEditRequest;
import com.peeerr.climbing.dto.post.PostSearchCondition;
import com.peeerr.climbing.dto.post.PostResponse;
import com.peeerr.climbing.dto.post.PostDetailResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import com.peeerr.climbing.exception.ex.UnauthorizedAccessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @DisplayName("게시물들을 카테고리와 검색어로 필터링해서 조회한다.")
    @Test
    void getPostsFilteredByCategoryIdAndSearchWord() throws Exception {
        //given
        Long categoryId = 1L;
        Pageable pageable = Pageable.ofSize(10);
        PostSearchCondition condition = PostSearchCondition.of("제목 검색 테스트", "본문 검색 테스트");

        given(postRepository.findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable)).willReturn(Page.empty());

        //when
        Page<PostResponse> posts = postService.getPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);

        //then
        assertThat(posts).isEmpty();

        then(postRepository).should().findPostsFilteredByCategoryIdAndSearchWord(categoryId, condition, pageable);
    }

    @DisplayName("게시물들을 필터링해서 조회하는데, 카테고리와 검색어가 주어지지 않으면 모든 게시물을 조회한다.")
    @Test
    void getPosts() throws Exception {
        //given
        Pageable pageable = Pageable.ofSize(10);

        given(postRepository.findPostsFilteredByCategoryIdAndSearchWord(null, null, pageable)).willReturn(Page.empty());

        //when
        Page<PostResponse> posts = postService.getPostsFilteredByCategoryIdAndSearchWord(null, null, pageable);

        //then
        assertThat(posts).isEmpty();

        then(postRepository).should().findPostsFilteredByCategoryIdAndSearchWord(null, null, pageable);
    }

    @DisplayName("게시물 하나를 조회해 온다.")
    @Test
    void getPostWithComments() throws Exception {
        //given
        Long postId = 1L;
        Post post = createPost(1L);

        given(postRepository.findPostById(postId)).willReturn(Optional.of(post));

        //when
        PostDetailResponse response = postService.getPostWithComments(postId);

        //then
        assertThat(response.getTitle()).isEqualTo("제목 테스트");
        assertThat(response.getContent()).isEqualTo("본문 테스트");

        then(postRepository).should().findPostById(postId);
    }

    @DisplayName("새로운 게시물 하나를 추가한다.")
    @Test
    void addPost() throws Exception {
        // given
        Long categoryId = 1L;
        PostCreateRequest request = PostCreateRequest.of("제목 테스트", "본문 테스트", categoryId);
        Category category = Category.builder().categoryName("자유 게시판").build();
        Member member = Member.builder()
                .username("test")
                .email("test@example.com")
                .password("test")
                .build();

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .category(category)
                .member(member)
                .build();

        given(postRepository.save(any(Post.class))).willReturn(post);
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        //when
        postService.addPost(request, member);

        //then
        then(postRepository).should().save(any(Post.class));
        then(categoryRepository).should().findById(categoryId);
    }

    @DisplayName("기존 게시물 하나를 수정한다.")
    @Test
    void editPost() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;
        Long categoryId = 2L;
        Post post = createPost(loginId);

        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", categoryId);
        Category category = Category.builder().id(categoryId).categoryName("후기 게시판").build();

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        //when
        postService.editPost(postId, request, loginId);

        //then
        assertThat(post.getTitle()).isEqualTo(request.getTitle());
        assertThat(post.getContent()).isEqualTo(request.getContent());
        assertThat(post.getCategory().getId()).isEqualTo(request.getCategoryId());

        then(postRepository).should().findById(postId);
        then(categoryRepository).should().findById(categoryId);
    }

    @DisplayName("[접근 권한X] 게시글 정보를 수정하는데, 로그인 사용자와 게시글 작성자가 다르면 예외를 던진다.")
    @Test
    void editPostWithoutPermission() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        Long categoryId = 2L;
        Post post = createPost(2L);

        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", categoryId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when & then
        assertThrows(UnauthorizedAccessException.class, () -> postService.editPost(postId, request, loginId));
        then(postRepository).should().findById(postId);
    }

    @DisplayName("id를 받아 게시물을 수정하는데, 해당하는 게시물이 없으면 예외를 던진다.")
    @Test
    void editPostWithNonExistPost() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", 2L);

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> postService.editPost(postId, request, loginId));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("게시물 id가 주어지면, 해당 게시물을 삭제한다.")
    @Test
    void removePost() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        Post post = createPost(loginId);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        willDoNothing().given(postRepository).delete(post);

        //when
        postService.removePost(postId, loginId);

        //then
        then(postRepository).should().findById(postId);
        then(postRepository).should().delete(post);
    }

    @DisplayName("[접근 권한X] 게시글을 삭제하는데, 로그인 사용자와 게시글 작성자가 다르면 예외를 던진다.")
    @Test
    void removePostWithoutPermission() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        Post post = createPost(2L);

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when & then
        assertThrows(UnauthorizedAccessException.class, () -> postService.removePost(postId, loginId));
        then(postRepository).should().findById(postId);
    }

    @DisplayName("id를 받아 게시물을 삭제하는데 해당하는 게시물이 없으면 예외를 던진다.")
    @Test
    void removePostWithNonExistPost() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class,
                () -> postService.removePost(postId, loginId));

        then(postRepository).should().findById(postId);
    }

    private Post createPost(Long loginId) {
        Category category = Category.builder().categoryName("자유 게시판").build();
        Post post = Post.builder()
                .member(Member.builder().id(loginId).build())
                .title("제목 테스트")
                .content("본문 테스트")
                .category(category)
                .build();

        return post;
    }

}
