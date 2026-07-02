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

        return DefaultResponse.<CategoryResponse>builder()
                .code("201")
                .title("SUCCESS")
                .message("Category Created successfully.")
                .data(categoryService.createCategory(categoryRequest))
                .build();
    }

    @GetMapping
    public DefaultResponse<List<CategoryResponse>> getAllCategories() {

        return DefaultResponse.<List<CategoryResponse>>builder()
                .code("00")
                .title("SUCCESS")
                .message("Success")
                .data(categoryService.getAllCategories())
                .build();
    }

    @PutMapping("/{id}")
    public DefaultResponse<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {

        return DefaultResponse.<CategoryResponse>builder()
                .code("00")
                .title("SUCCESS")
                .message("Category updated successfully")
                .data(categoryService.updateCategory(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    public DefaultResponse<CategoryResponse> deleteCategory(@Valid @PathVariable Long id) {
        return DefaultResponse.<CategoryResponse>builder()
                .code("00")
                .title("SUCCESS")
                .message("Category deleted successfully")
                .data(categoryService.deleteCategory(id))
                .build();
    }
}
