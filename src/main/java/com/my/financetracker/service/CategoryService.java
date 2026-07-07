package com.my.financetracker.service;

import com.my.financetracker.entity.Category;
import com.my.financetracker.models.requests.CategoryRequest;
import com.my.financetracker.models.responses.CategoryResponse;
import com.my.financetracker.models.responses.DefaultResponse;
import com.my.financetracker.repository.CategoryRepository;
import com.my.financetracker.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public DefaultResponse<CategoryResponse> createCategory(CategoryRequest request) {
        log.info("Creating new category request {}", request);

        try {
            Category existingCategory = categoryRepository.findByNameIgnoreCase(request.getName());

            if (existingCategory != null) {
                log.info("Category already exists {}", existingCategory.getName());

                return DefaultResponse.<CategoryResponse>builder()
                        .code(ResponseUtil.DUPLICATE_RESOURCE)
                        .title(ResponseUtil.FAILED)
                        .message("Category already exists.")
                        .build();
            }

            Category category = new Category();
            category.setName(request.getName());
            category.setType(request.getType());
            category.setIsDefault(true);

            Category savedCategory = categoryRepository.save(category);
            log.info("Created category {}", category);

            CategoryResponse response = mapToCategoryResponse(savedCategory);

            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Category created successfully.")
                    .data(response)
                    .build();

        } catch (Exception e) {
            log.error("Error while creating category {}", e.getMessage());
            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.FAILED_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable create category")
                    .data(null)
                    .build();

        }
    }


    public DefaultResponse<List<CategoryResponse>> getAllCategories() {

        log.info("Getting all categories");

        try {
            List<CategoryResponse> categoryResponses = categoryRepository.findAll()
                    .stream()
                    .map(category -> CategoryResponse.builder()
                            .id(category.getId())
                            .name(category.getName())
                            .type(category.getType())
                            .active(category.getIsDefault())
                            .build())
                    .toList();

            log.info("Found {} categories", categoryResponses.size());

            return DefaultResponse.<List<CategoryResponse>>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Categories fetched successfully")
                    .data(categoryResponses)
                    .build();

        } catch (Exception e) {
            log.error("Error while getting all categories", e);

            return DefaultResponse.<List<CategoryResponse>>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to fetch categories")
                    .data(null)
                    .build();
        }
    }


    public DefaultResponse<CategoryResponse> updateCategory(Long id, @Valid CategoryRequest request) {

        log.info("Updating category. id: {}", id);

        try {

            Category category = categoryRepository.findById(id)
                    .orElse(null);

            if (category == null) {
                return DefaultResponse.<CategoryResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Category not found")
                        .data(null)
                        .build();
            }

            Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCaseAndIdNot(request.getName(), id);

            if (existingCategory.isPresent()) {
                return DefaultResponse.<CategoryResponse>builder()
                        .code(ResponseUtil.DUPLICATE_RESOURCE)
                        .title(ResponseUtil.FAILED)
                        .message("Category already exists")
                        .data(null)
                        .build();
            }

            category.setName(request.getName());
            category.setType(request.getType());

            Category updatedCategory = categoryRepository.save(category);

            log.info("Category updated successfully. id: {}", updatedCategory.getId());

            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Category updated successfully")
                    .data(mapToCategoryResponse(updatedCategory))
                    .build();

        } catch (Exception e) {

            log.error("Error while updating category. id: {}", id, e);

            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to update category")
                    .data(null)
                    .build();
        }
    }


    public DefaultResponse<CategoryResponse> deleteCategory(Long id) {

        log.info("Deleting category. id: {}", id);

        try {

            Category category = categoryRepository.findById(id)
                    .orElse(null);

            if (category == null) {
                return DefaultResponse.<CategoryResponse>builder()
                        .code(ResponseUtil.NOT_FOUND_CODE)
                        .title(ResponseUtil.FAILED)
                        .message("Category not found")
                        .data(null)
                        .build();
            }

            categoryRepository.delete(category);

            log.info("Category deleted successfully. id: {}", id);

            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.SUCCESS_CODE)
                    .title(ResponseUtil.SUCCESS)
                    .message("Category deleted successfully")
                    .data(null)
                    .build();

        } catch (Exception e) {

            log.error("Error while deleting category. id: {}", id, e);

            return DefaultResponse.<CategoryResponse>builder()
                    .code(ResponseUtil.INTERNAL_ERROR_CODE)
                    .title(ResponseUtil.FAILED)
                    .message("Unable to delete category")
                    .data(null)
                    .build();
        }
    }

    private CategoryResponse mapToCategoryResponse(Category category) {

        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .active(category.getIsDefault())
                .build();
    }
}
