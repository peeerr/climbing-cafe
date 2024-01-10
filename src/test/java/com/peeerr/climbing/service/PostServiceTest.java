package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.post.PostRepository;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
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
    
    @DisplayName("게시물 전체를 페이징해서 조회해 온다.")
    @Test
    void getPosts() throws Exception {
        //given
        Pageable pageable = Pageable.ofSize(10);
        given(postRepository.findAll(pageable)).willReturn(Page.empty());
        
        //when
        Page<Post> posts = postService.getPosts(pageable);

        //then
        assertThat(posts).isEmpty();

        then(postRepository).should().findAll(pageable);
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 조회해 온다.")
    @Test
    void getPost() throws Exception {
        //given
        long postId = 1L;
        Post post = Post.of("제목 테스트", "본문 테스트");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when
        PostResponse response = postService.getPost(postId);

        //then
        assertThat(response.getTitle()).isEqualTo("제목 테스트");
        assertThat(response.getContent()).isEqualTo("본문 테스트");

        then(postRepository).should().findById(postId);
    }

    @DisplayName("해당하는 게시물 없으면 예외를 던진다.")
    @Test
    void getPostWithNonExistPostId() throws Exception {
        //given
        long postId = 1L;

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> postService.getPost(postId));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("새로운 게시물 하나를 추가한다.")
    @Test
    void addPost() throws Exception {
        // given
        Post post = Post.of("제목 테스트", "본문 테스트");

        given(postRepository.save(any(Post.class))).willReturn(post);

        //when
        postService.addPost(post);

        //then
        then(postRepository).should().save(any(Post.class));
    }

    @DisplayName("기존 게시물 하나를 수정한다.")
    @Test
    void editPost() throws Exception {
        //given
        Post post = Post.of("제목", "본문");
        long postId = 1L;
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트");

        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        //when
        postService.editPost(postId, request);

        //then
        assertThat(post.getTitle()).isEqualTo(request.getTitle());
        assertThat(post.getContent()).isEqualTo(request.getContent());

        then(postRepository).should().findById(postId);
    }

    @DisplayName("해당하는 게시물 없으면 예외를 던진다.")
    @Test
    void editPostWithNonExistPostId() throws Exception {
        //given
        long postId = 1L;
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트");

        given(postRepository.findById(postId)).willReturn(Optional.empty());

        //when & then
        assertThrows(EntityNotFoundException.class, () -> postService.editPost(postId, request));

        then(postRepository).should().findById(postId);
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 삭제한다.")
    @Test
    void removePost() throws Exception {
        //given
        long postId = 1L;
        willDoNothing().given(postRepository).deleteById(postId);

        //when
        postService.removePost(postId);

        //then
        then(postRepository).should().deleteById(postId);
    }

}
