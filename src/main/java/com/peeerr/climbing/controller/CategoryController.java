package com.peeerr.climbing.controller;

import com.peeerr.climbing.config.constant.MessageConstant;
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
    public ResponseEntity<List<CategoryResponse>> categoryList() {
        List<CategoryResponse> categories = categoryService.getCategories();

        return ResponseEntity.ok().body(categories);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> categoryDetail(@PathVariable Long categoryId) {
        CategoryResponse category = categoryService.getCategory(categoryId);

        return ResponseEntity.ok().body(category);
    }

    @PostMapping
    public ResponseEntity<Void> categoryAdd(@RequestBody @Valid CategoryCreateRequest categoryCreateRequest,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException(MessageConstant.VALIDATION_ERROR, errorMap);
        }

        categoryService.addCategory(categoryCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<Void> categoryEdit(@PathVariable Long categoryId,
                                          @RequestBody @Valid CategoryEditRequest categoryEditRequest,
                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();

            for (FieldError error: bindingResult.getFieldErrors()) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }

            throw new ValidationException(MessageConstant.VALIDATION_ERROR, errorMap);
        }

        categoryService.editCategory(categoryId, categoryEditRequest);

        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> categoryRemove(@PathVariable Long categoryId) {
        categoryService.removeCategory(categoryId);
        
        return ResponseEntity.ok().build();
    }

}
