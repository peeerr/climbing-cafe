package com.peeerr.climbing.controller;

import com.peeerr.climbing.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@WebMvcTest(controllers = FileController.class)
class FileControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileService fileService;

    @DisplayName("여러 개의 파일을 요청 받아 저장한다.")
    @Test
    void fileUpload() throws Exception {
        //given
        Long postId = 1L;
        MockMultipartFile file1 = new MockMultipartFile("files", "example1.jpg", "image/jpeg", "image1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "example2.jpg", "image/jpeg", "image2".getBytes());

        given(fileService.uploadFiles(anyLong(), any(List.class))).willReturn(Collections.emptyList());

        //when
        ResultActions result = mvc.perform(multipart("/api/posts/{postId}/files", postId)
                .file(file1)
                .file(file2)
                .with(csrf())
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("postId", String.valueOf(postId)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(fileService).should().uploadFiles(anyLong(), any(List.class));
    }

    @DisplayName("파일 id 를 받아 삭제 처리한다. (유저 권한 기준)")
    @Test
    void fileUpdateDeleteFlag() throws Exception {
        //given
        Long fileId = 1L;
        willDoNothing().given(fileService).updateDeleteFlag(fileId);

        //when
        ResultActions result = mvc.perform(delete("/api/posts/{postId}/files/{fileId}", 1L, fileId)
                .with(csrf()));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"));

        then(fileService).should().updateDeleteFlag(fileId);
    }

}