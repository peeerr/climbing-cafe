package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
import com.peeerr.climbing.dto.category.response.CategoryResponse;
import com.peeerr.climbing.dto.common.ApiResponse;
import com.peeerr.climbing.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
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
    public ResponseEntity<ApiResponse> categoryAdd(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest,
                                         BindingResult bindingResult) {
        CategoryResponse addedCategory = categoryService.addCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(addedCategory));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> categoryEdit(@PathVariable Long categoryId,
                                          @RequestBody @Valid CategoryEditRequest categoryEditRequest,
                                          BindingResult bindingResult) {
        CategoryResponse editedCategory = categoryService.editCategory(categoryId, categoryEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.success(editedCategory));
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponse> categoryRemove(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        
        return ResponseEntity.ok()
                .body(ApiResponse.success());
    }

}
