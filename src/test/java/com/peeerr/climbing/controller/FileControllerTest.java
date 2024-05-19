package com.peeerr.climbing.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peeerr.climbing.constant.ErrorMessage;
import com.peeerr.climbing.entity.Member;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.FileService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@AutoConfigureMockMvc
@SpringBootTest
class FileControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileService fileService;

    @DisplayName("게시물 ID에 해당되는 모든 파일 URL 을 조회한다.")
    @Test
    void fileUrlListByPost() throws Exception {
        //given
        given(fileService.getFilesByPostId(anyLong())).willReturn(List.of());

        Long postId = 1L;

        //when
        ResultActions result = mvc.perform(get("/api/posts/{postId}/files", postId)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("message").value("success"));

        then(fileService).should().getFilesByPostId(anyLong());
    }

    @DisplayName("여러 개의 파일을 요청 받아 저장한다.")
    @Test
    void fileUpload() throws Exception {
        //given
        Long postId = 1L;
        MockMultipartFile file1 = new MockMultipartFile("files", "example1.jpg", "image/jpeg", "image1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "example2.jpg", "image/jpeg", "image2".getBytes());

        willDoNothing().given(fileService).uploadFiles(anyLong(), anyLong(), any(List.class));

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(1L).build());

        //when
        ResultActions result = mvc.perform(multipart("/api/posts/{postId}/files", postId)
                .file(file1)
                .file(file2)
                .with(csrf())
                .with(user(userDetails))
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(fileService).should().uploadFiles(anyLong(), anyLong(), any(List.class));
    }

    @Test
    void fileUploadWithoutFile() throws Exception {
        //given
        Long postId = 1L;

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(1L).build());

        //when
        ResultActions result = mvc.perform(multipart("/api/posts/{postId}/files", postId)
            .file("files", null)
            .with(csrf())
            .with(user(userDetails))
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE));

        //then
        result
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(ErrorMessage.VALIDATION_ERROR))
            .andExpect(jsonPath("$.data.files").value(ErrorMessage.NO_FILE_SELECTED));
    }

    @DisplayName("파일 id 를 받아 삭제 처리한다. (유저 권한 기준)")
    @Test
    void fileUpdateDeleteFlag() throws Exception {
        //given
        Long fileId = 1L;
        Long loginId = 1L;
        willDoNothing().given(fileService).updateDeleteFlag(loginId, fileId);

        MemberPrincipal userDetails = new MemberPrincipal(Member.builder().id(loginId).build());

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}/files/{fileId}", 1L, fileId)
                .with(csrf())
                .with(user(userDetails)));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(fileService).should().updateDeleteFlag(loginId, fileId);
    }

}