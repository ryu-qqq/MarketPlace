package com.ryuqq.marketplace.adapter.in.rest.category.controller;

import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.CategorySearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryPathApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryTreeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.category.mapper.CategoryApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchQuery;
import com.ryuqq.marketplace.application.category.dto.query.CategoryTreeQuery;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPathResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResponse;
import com.ryuqq.marketplace.application.category.dto.response.CategoryTreeResponse;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryPathUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryTreeUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.GetCategoryUseCase;
import com.ryuqq.marketplace.application.category.port.in.query.SearchCategoryUseCase;
import com.ryuqq.marketplace.domain.category.exception.CategoryNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Category Public Query Controller
 *
 * <p>BFF 및 외부 서비스용 Public API (읽기 전용)</p>
 *
 * <p>GET 엔드포인트를 제공하며, ACTIVE + visible 상태의 카테고리만 조회합니다.</p>
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - 생성자 주입 명시적 작성</li>
 *   <li>Thin Controller - UseCase로 비즈니스 로직 위임</li>
 *   <li>캐싱 전제 설계</li>
 * </ul>
 *
 * @author Claude Code
 * @since 2025-11-27
 */
@RestController
@RequestMapping("/api/v1/catalog/categories")
public class CategoryPublicQueryController {

    private final GetCategoryUseCase getCategoryUseCase;
    private final GetCategoryTreeUseCase getCategoryTreeUseCase;
    private final GetCategoryPathUseCase getCategoryPathUseCase;
    private final SearchCategoryUseCase searchCategoryUseCase;
    private final CategoryApiMapper mapper;

    public CategoryPublicQueryController(
            GetCategoryUseCase getCategoryUseCase,
            GetCategoryTreeUseCase getCategoryTreeUseCase,
            GetCategoryPathUseCase getCategoryPathUseCase,
            SearchCategoryUseCase searchCategoryUseCase,
            CategoryApiMapper mapper) {
        this.getCategoryUseCase = getCategoryUseCase;
        this.getCategoryTreeUseCase = getCategoryTreeUseCase;
        this.getCategoryPathUseCase = getCategoryPathUseCase;
        this.searchCategoryUseCase = searchCategoryUseCase;
        this.mapper = mapper;
    }

    /**
     * 카테고리 트리 조회 (ACTIVE + visible만)
     *
     * <p>Public API는 활성화되고 표시 가능한 카테고리만 반환합니다.</p>
     *
     * @param department 부서 필터 (선택)
     * @param productGroup 상품 그룹 필터 (선택)
     * @return 카테고리 트리
     */
    @GetMapping("/tree")
    public ResponseEntity<ApiResponse<CategoryTreeApiResponse>> getCategoryTree(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String productGroup) {

        // Public은 includeInactive = false로 ACTIVE만 조회
        CategoryTreeQuery query = new CategoryTreeQuery(false, department, productGroup);
        CategoryTreeResponse response = getCategoryTreeUseCase.getTree(query);
        CategoryTreeApiResponse apiResponse = mapper.toTreeApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * Leaf 카테고리 목록 조회
     *
     * <p>상품 등록 가능한 최하위 카테고리 목록을 반환합니다.</p>
     *
     * @param department 부서 필터 (선택)
     * @param productGroup 상품 그룹 필터 (선택)
     * @return Leaf 카테고리 목록
     */
    @GetMapping("/leaf")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> getLeafCategories(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String productGroup) {

        CategorySearchQuery query = CategorySearchQuery.leafOnly(department, productGroup);
        List<CategoryResponse> responses = searchCategoryUseCase.searchLeaves(query);
        List<CategoryApiResponse> apiResponses = mapper.toApiResponseList(responses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
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
     * 코드로 카테고리 조회
     *
     * @param code 카테고리 코드
     * @return 카테고리 정보
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<ApiResponse<CategoryApiResponse>> getCategoryByCode(
            @PathVariable String code) {
        CategoryResponse response = getCategoryUseCase.getByCode(code)
                .orElseThrow(() -> new CategoryNotFoundException("code: " + code));
        CategoryApiResponse apiResponse = mapper.toApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 카테고리 경로 조회 (breadcrumb)
     *
     * <p>특정 카테고리의 조상 경로를 반환합니다.</p>
     * <p>예: 패션 &gt; 남성의류 &gt; 상의 &gt; 티셔츠</p>
     *
     * @param id 카테고리 ID
     * @return 카테고리 경로
     */
    @GetMapping("/{id}/path")
    public ResponseEntity<ApiResponse<CategoryPathApiResponse>> getCategoryPath(
            @PathVariable Long id) {
        CategoryPathResponse response = getCategoryPathUseCase.getPath(id);
        CategoryPathApiResponse apiResponse = mapper.toPathApiResponse(response);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 자식 카테고리 목록 조회
     *
     * <p>특정 카테고리의 직계 자식 목록을 반환합니다.</p>
     *
     * @param id 부모 카테고리 ID
     * @return 자식 카테고리 목록
     */
    @GetMapping("/{id}/children")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> getChildren(
            @PathVariable Long id) {

        // 부모 카테고리 존재 확인
        getCategoryUseCase.getById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // 자식 카테고리를 찾기 위해 Tree 조회 후 해당 노드의 children 반환
        // 여기서는 단순히 검색으로 대체 (parentId로 직접 조회하는 UseCase 추가 권장)
        CategorySearchQuery query = new CategorySearchQuery(
                null, null, null, null, null, null);
        List<CategoryResponse> allCategories = searchCategoryUseCase.search(null);
        List<CategoryResponse> children = allCategories.stream()
                .filter(c -> id.equals(c.parentId()))
                .toList();

        List<CategoryApiResponse> apiResponses = mapper.toApiResponseList(children);
        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
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

    /**
     * 증분 조회 (변경된 카테고리)
     *
     * <p>특정 시점 이후 변경된 카테고리 목록을 반환합니다.</p>
     * <p>캐시 갱신을 위한 증분 동기화에 사용됩니다.</p>
     *
     * @param since 기준 시점 (ISO 8601 형식)
     * @return 변경된 카테고리 목록
     */
    @GetMapping("/updated-since")
    public ResponseEntity<ApiResponse<List<CategoryApiResponse>>> getUpdatedSince(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {

        List<CategoryResponse> responses = searchCategoryUseCase.findUpdatedSince(since);
        List<CategoryApiResponse> apiResponses = mapper.toApiResponseList(responses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }
}
