package com.peeerr.climbing.service;

import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
import com.peeerr.climbing.dto.category.response.CategoryResponse;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        return CategoryResponse.from(category);
    }

    @Transactional
    public CategoryResponse addCategory(CategoryCreateRequest request) {
        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .build();

        Category savedCategory = categoryRepository.save(category);

        return CategoryResponse.from(savedCategory);
    }

    @Transactional
    public CategoryResponse editCategory(Long categoryId, CategoryEditRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        category.changeCategoryName(request.getCategoryName());

        return CategoryResponse.from(category);
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

}
