package com.peeerr.climbing.service;

import com.peeerr.climbing.domain.category.Category;
import com.peeerr.climbing.domain.category.CategoryRepository;
import com.peeerr.climbing.dto.category.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.CategoryEditRequest;
import com.peeerr.climbing.dto.category.CategoryResponse;
import com.peeerr.climbing.exception.constant.ErrorMessage;
import com.peeerr.climbing.exception.ex.DuplicationException;
import com.peeerr.climbing.exception.ex.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
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
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        category.changeCategoryName(request.getCategoryName());
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND));

        categoryRepository.delete(category);
    }

    public void validateDuplicateCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findCategoryByCategoryName(categoryName);

        category.ifPresent(foundCategory -> {
            throw new DuplicationException(ErrorMessage.CATEGORY_DUPLICATED);
        });
    }

}
