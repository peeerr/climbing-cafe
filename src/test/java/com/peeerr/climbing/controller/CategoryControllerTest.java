package com.peeerr.climbing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
import com.peeerr.climbing.dto.category.response.CategoryResponse;
import com.peeerr.climbing.service.CategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@AutoConfigureMockMvc
@SpringBootTest
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
                .andExpect(jsonPath("$.message").value("success"));

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
                .andExpect(jsonPath("$.message").value("success"));

        then(categoryService).should().getCategory(anyLong());
    }

    @DisplayName("새로운 카테고리를 추가한다.")
    @Test
    void categoryAdd() throws Exception {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.of("자유 게시판");

        given(categoryService.addCategory(any(CategoryCreateRequest.class))).willReturn(any(CategoryResponse.class));

        //when
        ResultActions result = mvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"));

        then(categoryService).should().addCategory(any(CategoryCreateRequest.class));
    }

    @DisplayName("id와 수정 정보를 받아서, 해당 id의 카테고리를 수정한다.")
    @Test
    void categoryEdit() throws Exception {
        //given
        Long categoryId = 1L;
        CategoryEditRequest request = CategoryEditRequest.of("후기 게시판");
        CategoryResponse response = CategoryResponse.from(Category.builder().categoryName("후기 게시판").build());

        given(categoryService.editCategory(anyLong(), any(CategoryEditRequest.class))).willReturn(response);

        //when
        ResultActions result = mvc.perform(put("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(request)));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(categoryService).should().editCategory(anyLong(), any(CategoryEditRequest.class));
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
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(categoryService).should().removeCategory(anyLong());
    }

}
