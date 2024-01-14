package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.dto.category.request.CategoryCreateRequest;
import com.peeerr.climbing.dto.category.request.CategoryEditRequest;
import com.peeerr.climbing.dto.category.response.CategoryResponse;
import com.peeerr.climbing.exception.ex.ValidationException;
import com.peeerr.climbing.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/categories")
@RestController
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<?> categoryList() {
        List<CategoryResponse> categories = categoryService.getCategories();

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "카테고리 전체 조회 성공", categories));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> categoryDetail(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategory(categoryId);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "카테고리 상세 조회 성공", category));
    }

    @PostMapping
    public ResponseEntity<?> categoryAdd(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException("유효성 검사 오류", errorMap);
        }

        categoryService.addCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("success", "카테고리 추가 성공", null));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> categoryEdit(@PathVariable Long categoryId,
                                          @RequestBody @Valid CategoryEditRequest categoryEditRequest,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException("유효성 검사 오류", errorMap);
        }

        categoryService.editCategory(categoryId, categoryEditRequest);

        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "카테고리 수정 성공", null));
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> categoryRemove(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        
        return ResponseEntity.ok()
                .body(ApiResponse.of("success", "카테고리 삭제 성공", null));
    }

}
