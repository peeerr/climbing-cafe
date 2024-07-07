package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.dto.category.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.CategoryEditRequest;
import com.peeerr.climbing.dto.category.CategoryResponse;
import com.peeerr.climbing.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse> categoryList() {
        List<CategoryResponse> categories = categoryService.getCategories();

        return ResponseEntity.ok()
                .body(ApiResponse.success(categories));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> categoryAdd(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        categoryService.addCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success());
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> categoryEdit(@PathVariable Long categoryId,
                                                    @RequestBody @Valid CategoryEditRequest categoryEditRequest) {
        categoryService.editCategory(categoryId, categoryEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> categoryRemove(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);

        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
