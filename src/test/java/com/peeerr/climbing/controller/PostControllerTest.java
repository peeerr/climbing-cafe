package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.dto.request.PostCreateRequest;
import com.peeerr.climbing.dto.request.PostEditRequest;
import com.peeerr.climbing.dto.request.PostSearchCondition;
import com.peeerr.climbing.dto.response.PostDetailResponse;
import com.peeerr.climbing.dto.response.PostResponse;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

    @DisplayName("[게시물 조회] - 카테고리와 검색어로 필터링해서 조회한다.")
    @Test
    void postListFilteredByCategoryIdAndSearchWord() throws Exception {
        //given
        PageImpl<PostResponse> pageImpl = new PageImpl<>(List.of(), PageRequest.of(0, 1), 0);
        given(postService.getPostsFilteredByCategoryIdAndSearchWord(anyLong(), any(PostSearchCondition.class), any(Pageable.class))).willReturn(pageImpl);

        //when
        ResultActions result = mvc.perform(get("/api/posts")
                .queryParam("categoryId", String.valueOf(1L))
                .queryParam("title", "제목 검색어")
                .queryParam("content", "본문 검색어"));

        //then
        result
                .andExpect(status().isOk())
                .andDo(print());

        then(postService).should().getPostsFilteredByCategoryIdAndSearchWord(anyLong(), any(PostSearchCondition.class), any(Pageable.class));
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 조회한다.")
    @Test
    void postDetail() throws Exception {
        //given
        Long categoryId = 1L;
        given(postService.getPost(anyLong())).willReturn(any(PostDetailResponse.class));

        //when
        ResultActions result = mvc.perform(get("/api/posts/{postId}", categoryId));

        //then
        result
                .andExpect(status().isOk())
                .andDo(print());

        then(postService).should().getPost(anyLong());
    }

    @DisplayName("새로운 게시물을 작성한다.")
    @Test
    void postAdd() throws Exception {
        //given
        PostCreateRequest request = PostCreateRequest.of("제목 테스트", "본문 테스트", 1L);

        Member member = Member.builder().username("test").build();
        MemberPrincipal userDetails = new MemberPrincipal(member);

        willDoNothing().given(postService).addPost(any(PostCreateRequest.class), any(Member.class));

        //when
        ResultActions result = mvc.perform(post("/api/posts")
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isCreated())
                .andDo(print());

        then(postService).should().addPost(any(PostCreateRequest.class), any(Member.class));
    }

    @DisplayName("게시물 id와 수정 정보가 주어지면 해당 게시물을 수정한다.")
    @Test
    void postEdit() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        PostEditRequest request = PostEditRequest.of("제목 수정 테스트", "본문 수정 테스트", 2L);

        Member member = Member.builder().id(loginId).username("test").build();
        MemberPrincipal userDetails = new MemberPrincipal(member);

        willDoNothing().given(postService).editPost(anyLong(), any(PostEditRequest.class), anyLong());

        //when
        ResultActions result = mvc.perform(put("/api/posts/{postId}", postId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isOk())
                .andDo(print());

        then(postService).should().editPost(anyLong(), any(PostEditRequest.class), anyLong());
    }

    @DisplayName("게시물 id가 주어지면 해당 게시물을 삭제한다.")
    @Test
    void postRemove() throws Exception {
        //given
        Long postId = 1L;
        Long loginId = 1L;

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(loginId).build());

        willDoNothing().given(postService).removePost(postId, loginId);

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}", postId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isOk())
                .andDo(print());

        then(postService).should().removePost(postId, loginId);
    }

}
