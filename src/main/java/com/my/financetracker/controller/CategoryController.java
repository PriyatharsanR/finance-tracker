package com.my.financetracker.controller;

import com.my.financetracker.models.requests.CategoryRequest;
import com.my.financetracker.models.responses.CategoryResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DefaultResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        return categoryService.createCategory(categoryRequest);
    }

    @GetMapping
    public DefaultResponse<List<CategoryResponse>> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/{id}")
    public DefaultResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public DefaultResponse<Void> deleteCategory(@Valid @PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
