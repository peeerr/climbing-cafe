package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.dto.post.response.PostResponse;
import com.peeerr.climbing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class)
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
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("게시물 전체 조회 성공"))
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
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("게시물 상세 조회 성공"))
                .andDo(print());

        then(postService).should().getPost(anyLong());
    }

    @DisplayName("새로운 게시물을 작성한다.")
    @Test
    void postAdd() throws Exception {
        //given
        PostCreateRequest request = PostCreateRequest.of("제목 테스트", "본문 테스트", 1L);

        willDoNothing().given(postService).addPost(any(PostCreateRequest.class));

        //when
        ResultActions result = mvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("게시물 작성 성공"))
                .andDo(print());

        then(postService).should().addPost(any(PostCreateRequest.class));
    }

    @DisplayName("새로운 게시물을 작성하는데, 제목 또는 본문이 비어 있으면 예외를 던진다.")
    @Test
    void postAddWithoutTitleOrContent() throws Exception {
        //given
        PostCreateRequest post = PostCreateRequest.of("", "   ", 1L);

        //when
        ResultActions result = mvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(post)));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("유효성 검사 오류"))
                .andDo(print());
    }

    @DisplayName("게시물 id와 수정 정보가 주어지면 해당 게시물을 수정한다.")
    @Test
    void editPost() throws Exception {
        //given
        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", 2L);

        willDoNothing().given(postService).editPost(anyLong(), any(PostEditRequest.class));

        //when
        ResultActions result = mvc.perform(put("/api/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("게시물 수정 성공"))
                .andDo(print());

        then(postService).should().editPost(anyLong(), any(PostEditRequest.class));
    }

    @DisplayName("기존 게시물을 수정하는데, 제목 또는 본문이 비어 있으면 예외를 던진다.")
    @Test
    void postEditWithoutTitleOrContent() throws Exception {
        //given
        PostEditRequest request = PostEditRequest.of("", "   ", 1L);

        //when
        ResultActions result = mvc.perform(put("/api/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("유효성 검사 오류"))
                .andDo(print());
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 삭제한다.")
    @Test
    void postRemove() throws Exception {
        //given
        willDoNothing().given(postService).removePost(anyLong());

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}", 1L));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("게시물 삭제 성공"))
                .andDo(print());

        then(postService).should().removePost(anyLong());
    }

}
