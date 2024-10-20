package com.webest.store.category.application;

import com.webest.store.category.presentation.dto.CategoryResponse;
import com.webest.store.category.presentation.dto.CreateCategoryRequest;
import com.webest.store.category.presentation.dto.UpdateCategoryRequest;
import com.webest.store.category.domain.model.StoreCategory;
import com.webest.store.category.domain.repository.CategoryRepository;
import com.webest.store.category.exception.CategoryErrorCode;
import com.webest.store.category.exception.CategoryException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;


    // 카테고리 생성
    @Transactional
    public CategoryResponse saveCategory(CreateCategoryRequest createCategoryRequest) {
        checkIfCategoryExistsByKey(createCategoryRequest.key());
        StoreCategory storeCategory = createCategoryRequest.toEntity();
        categoryRepository.save(storeCategory);
        return CategoryResponse.of(storeCategory);
    }

    // 카테고리 단건 조회
    public CategoryResponse getCategoryById(Long id) {
        StoreCategory storeCategory = findCategoryById(id);
        return CategoryResponse.of(storeCategory);
    }

    // 카테고리 전체 조회
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponse::of)
                .collect(Collectors.toList());
    }

    // 카테고리 수정
    @Transactional
    public CategoryResponse updateCategoryValue(UpdateCategoryRequest updateCategoryRequest) {
        StoreCategory category = findCategoryById(updateCategoryRequest.id());
        category.updateValue(updateCategoryRequest.value());
        return CategoryResponse.of(category);

    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long id) {
        StoreCategory category = findCategoryById(id);
        categoryRepository.delete(category);
    }

    // key 값으로 카테고리가 이미 존재하는지 확인하는 메서드
    private void checkIfCategoryExistsByKey(String categoryKey) {
        if (categoryRepository.existsByCategoryKey(categoryKey)) {
            throw new CategoryException(CategoryErrorCode.CATEGORY_ALREADY_EXISTS);
        }
    }

    // ID로 카테고리를 찾는 공통 메서드
    private StoreCategory findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CategoryErrorCode.CATEGORY_NOT_FOUND));
    }

}
