package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.config.auth.CustomUserDetails;
import com.peeerr.climbing.domain.user.Member;
import com.peeerr.climbing.dto.member.request.MemberCreateRequest;
import com.peeerr.climbing.dto.member.request.MemberEditRequest;
import com.peeerr.climbing.dto.member.response.MemberResponse;
import com.peeerr.climbing.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean
    private MemberService memberService;

    @DisplayName("회원 한 명을 추가한다.")
    @Test
    void memberAdd() throws Exception {
        //given
        Long memberId = 1L;
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");

        given(memberService.addMember(request)).willReturn(memberId);

        //when
        ResultActions result = mvc.perform(post("/api/members")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").value(memberId))
                .andDo(print());

        then(memberService).should().addMember(request);
    }

    @DisplayName("회원 정보를 받아, 회원 정보(username, email)를 수정한다.")
    @Test
    void memberEdit() throws Exception {
        //given
        Long memberId = 1L;
        String editUsername = "test";
        String editEmail = "test@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);
        MemberResponse response = MemberResponse.of(editUsername, editEmail);

        Member member = Member.builder()
                .id(memberId)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);

        given(memberService.editMember(memberId, request)).willReturn(response);

        //when
        ResultActions result = mvc.perform(put("/api/members/{memberId}", memberId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.username").value(editUsername))
                .andExpect(jsonPath("$.data.email").value(editEmail))
                .andDo(print());

        then(memberService).should().editMember(memberId, request);
    }

    @DisplayName("[접근 권한X] 회원 정보를 받아 회원 정보를 수정하는데, 해당 회원과 로그인 회원이 일치하지 않으면 예외를 던진다.")
    @Test
    void memberEditWithoutMatchingMember() throws Exception {
        //given
        Long memberId = 1L;
        String editUsername = "test";
        String editEmail = "test@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);

        Member member = Member.builder()
                .id(2L)
                .build();
        CustomUserDetails userDetails = new CustomUserDetails(member);

        //when
        ResultActions result = mvc.perform(put("/api/members/{memberId}", memberId)
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then
        result
                .andExpect(status().isUnauthorized())
                .andDo(print());
    }

}
