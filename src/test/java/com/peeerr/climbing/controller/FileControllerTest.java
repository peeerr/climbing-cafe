package com.peeerr.climbing.controller;

import com.peeerr.climbing.domain.Member;
import com.peeerr.climbing.security.MemberPrincipal;
import com.peeerr.climbing.service.FileService;
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

import java.util.List;

import static com.peeerr.climbing.exception.ValidationErrorMessage.FILE_REQUIRED;
import static com.peeerr.climbing.exception.ValidationErrorMessage.VALIDATION_ERROR;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(status().isOk());

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
                .andExpect(status().isCreated());

        then(fileService).should().uploadFiles(anyLong(), anyLong(), any(List.class));
    }

    @DisplayName("파일 업로드 시 파일을 첨부하지 않으면 예외를 던진다.")
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
                .andExpect(jsonPath("$.message").value(VALIDATION_ERROR))
                .andExpect(jsonPath("$.validation.files").value(FILE_REQUIRED));
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
                .andExpect(status().isOk());

        then(fileService).should().updateDeleteFlag(loginId, fileId);
    }

}
