package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.dto.user.request.MemberCreateRequest;
import com.peeerr.climbing.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

}
