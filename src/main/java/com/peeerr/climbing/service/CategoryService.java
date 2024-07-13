package com.peeerr.climbing.service;

import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
import com.peeerr.climbing.domain.Category;
import com.peeerr.climbing.exception.already.AlreadyExistsCategoryException;
import com.peeerr.climbing.exception.notfound.CategoryNotFoundException;
import com.peeerr.climbing.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public void addCategory(CategoryCreateRequest request) {
        validateDuplicateCategory(request.getCategoryName());

        Category category = Category.builder()
                .categoryName(request.getCategoryName())
                .build();

        categoryRepository.save(category);
    }

    @Transactional
    public void editCategory(Long categoryId, CategoryEditRequest request) {
        validateDuplicateCategory(request.getCategoryName());

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        category.changeCategoryName(request.getCategoryName());
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(CategoryNotFoundException::new);

        categoryRepository.delete(category);
    }

    public void validateDuplicateCategory(String categoryName) {
        categoryRepository.findCategoryByCategoryName(categoryName)
                .ifPresent(category -> {
                    throw new AlreadyExistsCategoryException();
                });
    }

}
