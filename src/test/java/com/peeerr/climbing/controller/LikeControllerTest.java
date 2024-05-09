package com.peeerr.climbing.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@SpringBootTest
class LikeControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LikeService likeService;

    @DisplayName("게시물 ID를 받아 해당 게시물에 달린 좋아요 수를 반환한다.")
    @Test
    void likeCount() throws Exception {
        //given
        Long postId = 1L;
        Long likeCount = 5L;

        given(likeService.getLikeCount(postId)).willReturn(likeCount);

        //when
        ResultActions result = mvc.perform(get("/api/posts/{postId}/likes/count", postId));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value(likeCount));
    }

    @DisplayName("게시물 ID를 받아 해당 게시물에 좋아요를 추가한다.")
    @Test
    void likeAdd() throws Exception {
        //given
        Long memberId = 1L;
        Long postId = 1L;

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(memberId).build());

        willDoNothing().given(likeService).like(memberId, postId);

        //when
        ResultActions result = mvc.perform(post("/api/posts/{postId}/likes", postId)
                .with(csrf())
                .with(user(userDetails)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(likeService).should().like(memberId, postId);
    }

    @DisplayName("게시물 ID를 받아 해당 게시물에 눌렀던 좋아요를 삭제한다.")
    @Test
    void likeRemove() throws Exception {
        //given
        Long memberId = 1L;
        Long postId = 1L;

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(memberId).build());

        willDoNothing().given(likeService).unlike(memberId, postId);

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}/likes", postId)
                .with(csrf())
                .with(user(userDetails)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(likeService).should().unlike(memberId, postId);
    }

}
