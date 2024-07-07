package com.peeerr.climbing.service;

import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
import com.peeerr.climbing.entity.Category;
import com.peeerr.climbing.exception.already.AlreadyExistsCategoryException;
import com.peeerr.climbing.exception.notfound.CategoryNotFoundException;
import com.peeerr.climbing.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new CategoryNotFoundException());

        category.changeCategoryName(request.getCategoryName());
    }

    @Transactional
    public void removeCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException());

        categoryRepository.delete(category);
    }

    public void validateDuplicateCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findCategoryByCategoryName(categoryName);

        category.ifPresent(foundCategory -> {
            throw new AlreadyExistsCategoryException();
        });
    }

}
