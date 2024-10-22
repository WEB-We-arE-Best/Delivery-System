package com.webest.store.store.presentation;

import com.webest.store.store.application.context.StoreStrategyContext;
import com.webest.store.store.presentation.dto.*;
import com.webest.store.store.application.StoreService;
import com.webest.web.common.UserRole;
import com.webest.web.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stores")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;
    private final StoreStrategyContext storeStrategyContext;

    // 가게 생성
    @PostMapping
    public CommonResponse<StoreResponse> saveStore(
            @Valid @RequestBody CreateStoreRequest request,
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role
            ) {
        UserRole userRole = UserRole.valueOf(role);
        StoreResponse response = storeService.saveStore(request, userId, userRole);
        return CommonResponse.success(response);
    }

    // 가게 주소 등록
    @PutMapping("/address")
    public CommonResponse<StoreResponse> updateStoreAddress(
            @Valid @RequestBody UpdateStoreAddressRequest request,
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role
    ) {
        UserRole userRole = UserRole.valueOf(role);
        StoreResponse response = storeService.updateStoreAddress(request, userId, userRole);
        return CommonResponse.success(response);
    }

    // 배달 범위 등록 (법정동)
    @PutMapping("/delivery-area/{id}")
    public CommonResponse<StoreResponse> registerDeliveryArea(
            @PathVariable("id") Long storeId,
            @RequestBody DeliveryAreaRequest requestDto
    ) {
        StoreResponse response = storeService.updateDeliveryArea(storeId, requestDto);
        return CommonResponse.success(response);
    }

    // 가게 단건 조회
    @GetMapping("/{id}")
    public CommonResponse<StoreResponse> getStoreById(@PathVariable("id") Long id) {
        StoreResponse response = storeService.getStoreById(id);
        return CommonResponse.success(response);
    }

    // 배달 가게 목록 조회 (MASTER - 전체 / OWNER & USER - 법정동 코드로 배달 가능한 가게만 조회)
    @GetMapping
    public CommonResponse<List<StoreResponse>> getDeliveryStores(
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role
    ) {
        // String을 UserRole로 변환
        UserRole userRole = UserRole.valueOf(role);
        List<StoreResponse> responses = storeStrategyContext.getDeliveryStores(userId, userRole);
        return CommonResponse.success(responses);
    }

    // 배달 가게 가게이름으로 검색
    @GetMapping("/search")
    public CommonResponse<List<StoreResponse>> searchDeliveryStores(
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword

    ) {
        // String을 UserRole로 변환
        UserRole userRole = UserRole.valueOf(role);
        List<StoreResponse> responses = storeStrategyContext.searchDeliveryStores(userId, userRole, categoryId, keyword);
        return CommonResponse.success(responses);
    }

    // 포장 가게 목록 조회 (MASTER - 전체 / OWNER & USER - REDIS GEO로 반경내 가게만 조회)
    @GetMapping("/take-out/{radius}")
    public CommonResponse<List<StoreResponse>> getTakeOutStores(
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role,
            @PathVariable("radius") Double radius
    ) {
        // String을 UserRole로 변환
        UserRole userRole = UserRole.valueOf(role);
        List<StoreResponse> responses = storeStrategyContext.getTakeOutStores(userId, userRole, radius);
        return CommonResponse.success(responses);
    }

    @GetMapping("/user")
    public CommonResponse<List<StoreResponse>> getStoresByUserAddressCode(
            @RequestHeader("X-UserId") String userId
    ) {
        List<StoreResponse> responses = storeService.getStoresByUserAddressCode(userId);
        return CommonResponse.success(responses);
    }

    @GetMapping("/coordinates")
    public CommonResponse<List<StoreResponse>> getStoreByCoordinates(@RequestBody Coordinates coordinates) {
        List<StoreResponse> responses = storeService.getStoresByCoordinates(coordinates);
        return CommonResponse.success(responses);
    }

    // 가게 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(
            @PathVariable("id") Long id,
            @RequestHeader("X-UserId") String userId,
            @RequestHeader("X-Role") String role
    ) {

        UserRole userRole = UserRole.valueOf(role);
        storeService.deleteStore(id, userId, userRole);
        return ResponseEntity.noContent().build();
    }


}
