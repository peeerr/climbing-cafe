package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.service.validator.CategoryValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryValidator categoryValidator;

    @InjectMocks
    private CategoryService categoryService;

    @DisplayName("카테고리를 전체 조회해 온다.")
    @Test
    void getCategories() throws Exception {
        //given
        given(categoryRepository.findAll()).willReturn(List.of());

        //when
        List<CategoryResponse> categories = categoryService.getCategories();

        //then
        assertThat(categories).isEmpty();

        then(categoryRepository).should().findAll();
    }

    @DisplayName("새로운 카테고리 하나를 추가한다.")
    @Test
    void addCategory() throws Exception {
        //given
        CategoryCreateRequest request = CategoryCreateRequest.of("자유 게시판");
        Category category = Category.builder().categoryName(request.getCategoryName()).build();

        willDoNothing().given(categoryValidator).validateCategoryNameUnique(anyString());
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        //when
        categoryService.addCategory(request);

        //then
        then(categoryValidator).should().validateCategoryNameUnique(anyString());
        then(categoryRepository).should().save(any(Category.class));
    }

    @DisplayName("수정 정보를 받아 카테고리를 수정한다.")
    @Test
    void editCategory() throws Exception {
        //given
        Long categoryId = 1L;
        Category category = Category.builder().categoryName("자유 게시판").build();
        CategoryEditRequest request = CategoryEditRequest.of("후기 게시판");

        willDoNothing().given(categoryValidator).validateCategoryNameUnique(anyString());
        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));

        //when
        categoryService.editCategory(categoryId, request);

        //then
        then(categoryValidator).should().validateCategoryNameUnique(anyString());
        assertThat(category.getCategoryName()).isEqualTo(request.getCategoryName());

        then(categoryRepository).should().findById(categoryId);
    }

    @DisplayName("id를 받아 카테고리를 수정하는데, 해당하는 카테고리가 없으면 예외를 던진다.")
    @Test
    void editPostWithNonExistCategory() throws Exception {
        //given
        Long categoryId = 1L;
        CategoryEditRequest request = CategoryEditRequest.of("후기 게시판");

        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> categoryService.editCategory(categoryId, request));

        then(categoryRepository).should().findById(categoryId);
    }

    @DisplayName("카테고리 id가 주어지면, 해당 카테고리를 삭제한다.")
    @Test
    void removeCategory() throws Exception {
        //given
        Long categoryId = 1L;
        Category category = Category.builder().categoryName("자유 게시판").build();

        given(categoryRepository.findById(categoryId)).willReturn(Optional.of(category));
        willDoNothing().given(categoryRepository).delete(category);

        //when
        categoryService.removeCategory(categoryId);

        //then
        then(categoryRepository).should().findById(categoryId);
        then(categoryRepository).should().delete(category);
    }

    @DisplayName("id를 받아 카테고리를 삭제하는데 해당하는 카테고리가 없으면 예외를 던진다.")
    @Test
    void removePostWithNonExistCategory() throws Exception {
        //given
        Long categoryId = 1L;

        given(categoryRepository.findById(categoryId)).willReturn(Optional.empty());

        //when & then
        assertThatExceptionOfType(ClimbingException.class)
                .isThrownBy(() -> categoryService.removeCategory(categoryId));

        then(categoryRepository).should().findById(categoryId);
    }

}
