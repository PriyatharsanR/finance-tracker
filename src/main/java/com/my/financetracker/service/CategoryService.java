package com.my.financetracker.service;

import com.my.financetracker.entity.Category;
import com.my.financetracker.exception.ResourceNotFoundException;
import com.my.financetracker.models.requests.CategoryRequest;
import com.my.financetracker.models.responses.CategoryResponse;
import com.my.financetracker.repository.CategoryRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest request) {
        log.info("Creating new category request {}", request);
        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setIsDefault(true);

        categoryRepository.save(category);
        log.info("Created category {}", category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .active(category.getIsDefault())
                .build();
    }


    public List<CategoryResponse> getAllCategories() {
        log.info("Getting all categories");
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            log.info("No categories found");
            return null;
        }

        log.info("Found {} categories", categories.size());
        return categories.stream()
                .map(category -> CategoryResponse.builder()
                        .id(category.getId())
                        .name(category.getName())
                        .type(category.getType())
                        .active(category.getIsDefault())
                        .build())
                .toList();
    }


    public CategoryResponse updateCategory(Long id, @Valid CategoryRequest request) {
        log.info("Updating category request {}", request);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setType(request.getType());
        categoryRepository.save(category);
        log.info("Updated category {}", category);

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .active(category.getIsDefault())
                .build();
    }


    public CategoryResponse deleteCategory(@Valid Long id) {
        log.info("Deleting category request {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("category not found for deletion this id: " + id));
        categoryRepository.delete(category);

        log.info("Deleted category {}", category);
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }
}
