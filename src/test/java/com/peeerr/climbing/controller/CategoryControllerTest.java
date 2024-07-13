package com.peeerr.climbing.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peeerr.climbing.entity.Category;
import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
import com.peeerr.climbing.service.CategoryService;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WithMockUser
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

    @DisplayName("새로운 카테고리를 추가한다.")
    @Test
    void categoryAdd() throws Exception {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.of("자유 게시판");

        willDoNothing().given(categoryService).addCategory(any(CategoryCreateRequest.class));

        //when
        ResultActions result = mvc.perform(post("/api/categories")
                .with(csrf())
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

        willDoNothing().given(categoryService).editCategory(anyLong(), any(CategoryEditRequest.class));

        //when
        ResultActions result = mvc.perform(put("/api/categories/{categoryId}", categoryId)
                .with(csrf())
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
        ResultActions result = mvc.perform(delete("/api/categories/{categoryId}", categoryId)
                .with(csrf()));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("success"))
                .andDo(print());

        then(categoryService).should().removeCategory(anyLong());
    }

}
