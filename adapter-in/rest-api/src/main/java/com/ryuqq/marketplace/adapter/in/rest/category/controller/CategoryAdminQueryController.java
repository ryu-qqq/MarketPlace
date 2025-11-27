package com.ryuqq.marketplace.adapter.in.rest.category.controller;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.CategorySearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryTreeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.mapper.CategoryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.category.dto.query.CategoryTreeQuery;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeResponse;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryTreeUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.SearchCategoryUseCase;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category Admin Query Controller
 *
 * <p>카테고리 관리용 Admin API (조회)</p>
 *
 * <p>GET 엔드포인트를 제공합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - 생성자 주입 명시적 작성</li>
 *   <li>Thin Controller - UseCase로 비즈니스 로직 위임</li>
 *   <li>Admin API는 모든 상태의 카테고리 조회 가능</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
@RestController
@RequestMapping("/api/v1/admin/catalog/categories")
public class CategoryAdminQueryController {

    private final GetCategoryUseCase getCategoryUseCase;
    private final GetCategoryTreeUseCase getCategoryTreeUseCase;
    private final SearchCategoryUseCase searchCategoryUseCase;
    private final CategoryApiMapper mapper;

    public CategoryAdminQueryController(
            GetCategoryUseCase getCategoryUseCase,
            GetCategoryTreeUseCase getCategoryTreeUseCase,
            SearchCategoryUseCase searchCategoryUseCase,
            CategoryApiMapper mapper) {
        this.getCategoryUseCase = getCategoryUseCase;
        this.getCategoryTreeUseCase = getCategoryTreeUseCase;
        this.searchCategoryUseCase = searchCategoryUseCase;
        this.mapper = mapper;
    }

    /**
     * 카테고리 트리 전체 조회 (상태 무관)
     *
     * <p>관리자용으로 모든 상태의 카테고리를 트리 구조로 반환합니다.</p>
     *
     * @param department 부서 필터 (선택)
     * @param productGroup 상품 그룹 필터 (선택)
     * @return 카테고리 트리
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<CategoryTreeApiResponse>> getCategoryTree(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String productGroup) {

        // Admin은 includeInactive = true로 모든 상태 조회
        CategoryTreeQuery query = new CategoryTreeQuery(true, department, productGroup);
        CategoryTreeResponse response = getCategoryTreeUseCase.getTree(query);
        CategoryTreeApiResponse apiResponse = mapper.toTreeApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 단일 카테고리 조회
     *
     * @param id 카테고리 ID
     * @return 카테고리 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> getCategory(@PathVariable Long id) {
        CategoryResponse response = getCategoryUseCase.getById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 검색
     *
     * @param request 검색 조건
     * @return 검색된 카테고리 목록
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> searchCategories(
            @ModelAttribute CategorySearchApiRequest request) {

        List<CategoryResponse> responses = searchCategoryUseCase.search(request.keyword());
        List<CategoryApiResponse> apiResponses = mapper.toApiResponseList(responses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
