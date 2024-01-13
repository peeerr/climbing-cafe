package com.peeerr.climbing.controller;

import com.peeerr.climbing.dto.ApiResponse;
import com.peeerr.climbing.dto.category.CategoryResponse;
import com.peeerr.climbing.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

}
