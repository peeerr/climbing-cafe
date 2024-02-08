package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.post.Post;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private PostService postService;

    @DisplayName("게시물 전체를 조회해 온다.")
    @Test
    void postList() throws Exception {
        //given
        given(postService.getPosts(any(Pageable.class))).willReturn(any(PageImpl.class));

        //when
        ResultActions result = mvc.perform(get("/api/posts")
                .queryParam("page", String.valueOf(0))
                .queryParam("size", String.valueOf(10))
                .queryParam("sort", "id,desc"));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(postService).should().getPosts(any(Pageable.class));
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 조회한다.")
    @Test
    void postDetail() throws Exception {
        //given
        Long categoryId = 1L;
        given(postService.getPost(anyLong())).willReturn(any(PostResponse.class));

        //when
        ResultActions result = mvc.perform(get("/api/posts/{postId}", categoryId));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(postService).should().getPost(anyLong());
    }

    @DisplayName("새로운 게시물을 작성한다.")
    @Test
    void postAdd() throws Exception {
        //given
        PostCreateRequest request = PostCreateRequest.of("제목 테스트", "본문 테스트", 1L);
        CustomUserDetails userDetails = new CustomUserDetails(Member.builder().build());

        Category category = Category.builder().categoryName("자유 게시판").build();
        Post post = Post.builder().category(category).build();
        PostResponse response = PostResponse.from(post);

        given(postService.addPost(any(PostCreateRequest.class), any(Member.class))).willReturn(response);

        //when
        ResultActions result = mvc.perform(post("/api/posts")
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(postService).should().addPost(any(PostCreateRequest.class), any(Member.class));
    }

    @DisplayName("게시물 id와 수정 정보가 주어지면 해당 게시물을 수정한다.")
    @Test
    void postEdit() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", 2L);

        Category category = Category.builder().categoryName("자유 게시판").build();
        Post post = Post.builder().category(category).build();
        PostResponse response = PostResponse.from(post);

        // Post Owner == Login Member
        Member member = Member.builder()
                .id(memberId)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);
        given(postService.getMember(postId)).willReturn(member.getId());

        given(postService.editPost(anyLong(), any(PostEditRequest.class))).willReturn(response);

        //when
        ResultActions result = mvc.perform(put("/api/posts/{postId}", postId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(postService).should().getMember(postId);
        then(postService).should().editPost(anyLong(), any(PostEditRequest.class));
    }

    @DisplayName("[접근 권한X] 게시물을 수정하는데, 해당 게시물 소유자와 로그인 회원이 다르면 예외를 던진다.")
    @Test
    void postEditWithoutMatchingMember() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", 2L);

        // Post Owner != Login Member
        Member member = Member.builder()
                .id(memberId)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);
        given(postService.getMember(postId)).willReturn(2L);

        //when
        ResultActions result = mvc.perform(put("/api/posts/{postId}", postId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andDo(print());

        then(postService).should().getMember(postId);
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 삭제한다.")
    @Test
    void postRemove() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;

        // Post Owner == Login Member
        Member member = Member.builder()
                .id(memberId)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);
        given(postService.getMember(postId)).willReturn(member.getId());

        willDoNothing().given(postService).removePost(postId);

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}", postId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(postService).should().getMember(postId);
        then(postService).should().removePost(postId);
    }

    @DisplayName("[접근 권한X] 게시물을 삭제하는데, 해당 게시물 소유자와 로그인 회원이 다르면 예외를 던진다.")
    @Test
    void postRemoveWithoutMatchingMember() throws Exception {
        //given
        Long postId = 1L;
        Long memberId = 1L;

        // Post Owner != Login Member
        Member member = Member.builder()
                .id(memberId)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);
        given(postService.getMember(postId)).willReturn(2L);

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}", postId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andDo(print());

        then(postService).should().getMember(postId);
    }

}
