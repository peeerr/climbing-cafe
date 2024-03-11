package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.comment.request.CommentCreateRequest;
import com.peeerr.climbing.dto.comment.request.CommentEditRequest;
import com.peeerr.climbing.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(controllers = CommentController.class)
class CommentControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean
    private CommentService commentService;

    @DisplayName("댓글 하나를 추가한다.")
    @Test
    void commentAdd() throws Exception {
        //given
        CommentCreateRequest request = CommentCreateRequest.of(1L, 1L, "댓글 테스트");

        willDoNothing().given(commentService).addComment(any(CommentCreateRequest.class), any(Member.class));

        CustomUserDetails userDetails = new CustomUserDetails(Member.builder().build());

        //when
        ResultActions result = mvc.perform(post("/api/comments")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(commentService).should().addComment(any(CommentCreateRequest.class), any(Member.class));
    }

    @DisplayName("댓글 하나를 수정한다.")
    @Test
    void commentEdit() throws Exception {
        //given
        Long commentId = 1L;
        CommentEditRequest request = CommentEditRequest.of("댓글 수정 테스트");
        Long loginId = 1L;

        CustomUserDetails userDetails = new CustomUserDetails(Member.builder().id(loginId).build());

        willDoNothing().given(commentService).editComment(anyLong(), any(CommentEditRequest.class), anyLong());

        //when
        ResultActions result = mvc.perform(put("/api/comments/{commentId}", commentId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request))
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(commentService).should().editComment(anyLong(), any(CommentEditRequest.class), anyLong());
    }

    @DisplayName("댓글 하나를 삭제한다.")
    @Test
    void commentRemove() throws Exception {
        //given
        Long commentId = 1L;
        Long loginId = 1L;

        CustomUserDetails userDetails = new CustomUserDetails(Member.builder().id(loginId).build());

        willDoNothing().given(commentService).removeComment(anyLong(), anyLong());

        //when
        ResultActions result = mvc.perform(delete("/api/comments/{commentId}", commentId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(commentService).should().removeComment(anyLong(), anyLong());
    }

}