package com.webest.store.category.presentation;

import com.webest.store.common.aop.RoleCheck;
import com.webest.store.category.presentation.dto.CategoryResponse;
import com.webest.store.category.presentation.dto.CreateCategoryRequest;
import com.webest.store.category.presentation.dto.UpdateCategoryRequest;
import com.webest.store.category.application.CategoryService;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;

    // 카테고리 생성
    @PostMapping
    @RoleCheck(requiredRole = UserRole.MASTER)
    public CommonResponse<CategoryResponse> saveCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest
    ) {
         CategoryResponse categoryResponse = categoryService.saveCategory(createCategoryRequest);
        return CommonResponse.success(categoryResponse);
    }

    // 카테고리 단건 조회
    @GetMapping("{id}")
    public CommonResponse<CategoryResponse> getCategoryById(@PathVariable("id") Long id) {
        CategoryResponse categoryResponse = categoryService.getCategoryById(id);
        return CommonResponse.success(categoryResponse);
    }

    // 카테고리 전체 조회
    @GetMapping
    public CommonResponse<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categoryResponses = categoryService.getAllCategories();
        return CommonResponse.success(categoryResponses);
    }

    // 카테고리 수정
    @PutMapping
    @RoleCheck(requiredRole = UserRole.MASTER)
    public CommonResponse<CategoryResponse> updateCategory(
            @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest
    ) {
        CategoryResponse categoryResponse = categoryService.updateCategoryValue(updateCategoryRequest);
        return CommonResponse.success(categoryResponse);
    }

    // 카테고리 삭제
    @DeleteMapping("{id}")
    @RoleCheck(requiredRole = UserRole.MASTER)
    public ResponseEntity<Void> deleteCategory(
            @PathVariable("id") Long id
    ) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

}
