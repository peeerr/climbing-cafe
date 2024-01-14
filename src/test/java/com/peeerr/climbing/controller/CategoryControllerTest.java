package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
import com.peeerr.climbing.dto.category.response.CategoryResponse;
import com.peeerr.climbing.dto.post.request.PostCreateRequest;
import com.peeerr.climbing.dto.post.request.PostEditRequest;
import com.peeerr.climbing.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private CategoryService categoryService;

    @DisplayName("모든 카테고리 조회를 해 온다.")
    @Test
    void categoryList() throws Exception {
        //given
        given(categoryService.getCategories()).willReturn(List.of());

        //when
        ResultActions result = mvc.perform(get("/api/categories"));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("카테고리 전체 조회 성공"));

        then(categoryService).should().getCategories();
    }

    @DisplayName("id가 주어지면, 카테고리 하나를 조회해 온다.")
    @Test
    void categoryDetail() throws Exception {
        //given
        Long categoryId = 1L;

        given(categoryService.getCategory(anyLong())).willReturn(any(CategoryResponse.class));

        //when
        ResultActions result = mvc.perform(get("/api/categories/{categoryId}", categoryId));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("카테고리 상세 조회 성공"));

        then(categoryService).should().getCategory(anyLong());
    }

    @DisplayName("새로운 카테고리를 추가한다.")
    @Test
    void categoryAdd() throws Exception {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.of("자유 게시판");

        willDoNothing().given(categoryService).addCategory(any(CategoryCreateRequest.class));

        //when
        ResultActions result = mvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("카테고리 추가 성공"));
        
        then(categoryService).should().addCategory(any(CategoryCreateRequest.class));
    }

    @DisplayName("새로운 카테고리를 추가하는데, 카테고리명이 비어 있으면 예외를 던진다.")
    @Test
    void categoryAddWithoutCategoryName() throws Exception {
        //given
        CategoryCreateRequest post = CategoryCreateRequest.of(" ");

        //when
        ResultActions result = mvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(post)));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("유효성 검사 오류"))
                .andDo(print());
    }

    @DisplayName("id와 수정 정보를 받아서, 해당 id의 카테고리를 수정한다.")
    @Test
    void categoryEdit() throws Exception {
        //given
        Long categoryId = 1L;
        CategoryEditRequest request = CategoryEditRequest.of("후기 게시판");

        willDoNothing().given(categoryService).editCategory(anyLong(), any(CategoryEditRequest.class));

        //when
        ResultActions result = mvc.perform(put("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("카테고리 수정 성공"))
                .andDo(print());

        then(categoryService).should().editCategory(anyLong(), any(CategoryEditRequest.class));
    }

    @DisplayName("기존 카테고리를 수정하는데, 카테고리명이 비어 있으면 예외를 던진다.")
    @Test
    void categoryEditWithoutCategoryName() throws Exception {
        //given
        CategoryEditRequest request = CategoryEditRequest.of("");

        //when
        ResultActions result = mvc.perform(put("/api/categories/{categoryId}", 1L)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").value("유효성 검사 오류"))
                .andDo(print());
    }

    @DisplayName("id가 주어지면, 해당 카테고리를 삭제한다.")
    @Test
    void categoryRemove() throws Exception {
        //given
        Long categoryId = 1L;
        willDoNothing().given(categoryService).removeCategory(anyLong());

        //when
        ResultActions result = mvc.perform(delete("/api/categories/{categoryId}", categoryId));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("카테고리 삭제 성공"))
                .andDo(print());

        then(categoryService).should().removeCategory(anyLong());
    }

}
