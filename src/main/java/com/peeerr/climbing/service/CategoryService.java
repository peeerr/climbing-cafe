package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
import com.peeerr.climbing.exception.ClimbingException;
import com.peeerr.climbing.exception.ErrorCode;
import com.peeerr.climbing.repository.CategoryRepository;
import com.peeerr.climbing.service.validator.CategoryValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryValidator categoryValidator;

    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ClimbingException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    public void addCategory(CategoryCreateRequest request) {
        categoryValidator.validateCategoryNameUnique(request.getCategoryName());

        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .build();

        categoryRepository.save(category);
    }

    public void editCategory(Long categoryId, CategoryEditRequest request) {
        categoryValidator.validateCategoryNameUnique(request.getCategoryName());

        Category category = getCategoryById(categoryId);

        category.changeCategoryName(request.getCategoryName());
    }

    public void removeCategory(Long categoryId) {
        Category category = getCategoryById(categoryId);

        categoryRepository.delete(category);
    }

}
