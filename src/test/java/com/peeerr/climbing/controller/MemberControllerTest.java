package com.peeerr.climbing.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.dto.request.MemberCreateRequest;
import com.peeerr.climbing.dto.request.MemberEditRequest;
import com.peeerr.climbing.security.MemberPrincipal;
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
        MemberCreateRequest request = MemberCreateRequest.of("test", "test1234", "test1234", "test@example.com");

        willDoNothing().given(memberService).addMember(request);

        //when
        ResultActions result = mvc.perform(post("/api/members/register")
                .with(csrf())
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(memberService).should().addMember(request);
    }

    @DisplayName("회원 정보를 받아, 회원 정보(username, email)를 수정한다.")
    @Test
    void memberEdit() throws Exception {
        //given
        Long memberId = 1L;
        Long loginId = 1L;

        String editUsername = "test";
        String editEmail = "test@example.com";
        MemberEditRequest request = MemberEditRequest.of(editUsername, editEmail);

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(loginId).build());

        willDoNothing().given(memberService).editMember(anyLong(), any(MemberEditRequest.class), anyLong());

        //when
        ResultActions result = mvc.perform(put("/api/members/{memberId}", memberId)
                .with(csrf())
                .with(SecurityMockMvcRequestPostProcessors.user(userDetails))
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(memberService).should().editMember(anyLong(), any(MemberEditRequest.class), anyLong());
    }

}
