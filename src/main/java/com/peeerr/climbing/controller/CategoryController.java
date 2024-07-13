package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.dto.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.request.CategoryEditRequest;
import com.peeerr.climbing.dto.response.CategoryResponse;
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
                .body(ApiResponse.of(categories));
    }

    @PostMapping
    public ResponseEntity<Void> categoryAdd(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest) {
        categoryService.addCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> categoryEdit(@PathVariable Long categoryId,
                                             @RequestBody @Valid CategoryEditRequest categoryEditRequest) {
        categoryService.editCategory(categoryId, categoryEditRequest);

        return ResponseEntity.ok()
                .build();
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> categoryRemove(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);

        return ResponseEntity.ok()
                .build();
    }

}
