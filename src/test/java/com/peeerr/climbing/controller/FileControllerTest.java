package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.file.request.FileUploadRequest;
import com.peeerr.climbing.service.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        willDoNothing().given(fileService).uploadFiles(any(FileUploadRequest.class));

        //when
        ResultActions result = mvc.perform(multipart("/api/files")
                .file(file1)
                .file(file2)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("postId", String.valueOf(postId)));

        //then
        result
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("파일 업로드 성공"));

        then(fileService).should().uploadFiles(any(FileUploadRequest.class));
    }

    @DisplayName("여러 개의 파일을 요청 받아 저장하는데, 첨부된 파일이 없으면 예외를 발생시킨다.")
    @Test
    void fileUploadWithNotingFile() throws Exception {
        //given
        Long postId = 1L;

        //when
        ResultActions result = mvc.perform(multipart("/api/files")
                .file("files", null)
                .file("files", null)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .param("postId", String.valueOf(postId)));

        //then
        result
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("유효성 검사 오류"));
    }

    @DisplayName("파일 id 를 받아 삭제 처리한다. (유저 권한 기준)")
    @Test
    void fileUpdateDeleteFlag() throws Exception {
        //given
        Long fileId = 1L;
        willDoNothing().given(fileService).updateDeleteFlag(fileId);

        //when
        ResultActions result = mvc.perform(delete("/api/files/{fileId}", fileId));

        //then
        result
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("파일 삭제 성공"));

        then(fileService).should().updateDeleteFlag(fileId);
    }

}