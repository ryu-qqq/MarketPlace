package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.AliasMatchApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandSimpleApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchQuery;
import com.ryuqq.marketplace.application.brand.port.in.query.GetBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.in.query.ResolveAliasUseCase;
import com.ryuqq.marketplace.application.brand.port.in.query.SearchBrandUseCase;
import com.ryuqq.marketplace.domain.brand.exception.BrandNotFoundException;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Brand Public Query Controller
 *
 * <p>브랜드 조회용 Public API (외부 서비스용)
 *
 * <p>status=ACTIVE인 브랜드만 노출됩니다.
 * 캐싱 전제로 설계되었습니다.
 */
@RestController
@RequestMapping("/api/v1/catalog/brands")
public class BrandPublicQueryController {

    private static final String ACTIVE_STATUS = "ACTIVE";

    private final GetBrandUseCase getBrandUseCase;
    private final SearchBrandUseCase searchBrandUseCase;
    private final ResolveAliasUseCase resolveAliasUseCase;
    private final BrandApiMapper brandApiMapper;

    public BrandPublicQueryController(
            GetBrandUseCase getBrandUseCase,
            SearchBrandUseCase searchBrandUseCase,
            ResolveAliasUseCase resolveAliasUseCase,
            BrandApiMapper brandApiMapper) {
        this.getBrandUseCase = getBrandUseCase;
        this.searchBrandUseCase = searchBrandUseCase;
        this.resolveAliasUseCase = resolveAliasUseCase;
        this.brandApiMapper = brandApiMapper;
    }

    /**
     * 브랜드 상세 조회
     *
     * @param id 브랜드 ID
     * @return 브랜드 상세 정보
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDetailApiResponse>> getBrand(@PathVariable Long id) {
        var brandDetailResponse = getBrandUseCase.getById(id);

        // ACTIVE가 아니면 Not Found 처리
        if (!ACTIVE_STATUS.equals(brandDetailResponse.status())) {
            throw new BrandNotFoundException(id);
        }

        var apiResponse = brandApiMapper.toDetailApiResponse(brandDetailResponse);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 코드로 조회
     *
     * @param code 브랜드 코드
     * @return 브랜드 상세 정보
     */
    @GetMapping("/by-code/{code}")
    public ResponseEntity<ApiResponse<BrandDetailApiResponse>> getBrandByCode(@PathVariable String code) {
        var brandDetailResponse = getBrandUseCase.getByCode(code);

        // ACTIVE가 아니면 Not Found 처리
        if (!ACTIVE_STATUS.equals(brandDetailResponse.status())) {
            throw new BrandNotFoundException(code);
        }

        var apiResponse = brandApiMapper.toDetailApiResponse(brandDetailResponse);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }

    /**
     * 브랜드 검색 (ACTIVE만)
     *
     * @param keyword 검색어
     * @param department 부문 필터
     * @param isLuxury 럭셔리 필터
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return 검색 결과 (ACTIVE만)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<BrandApiResponse>>> searchBrands(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Boolean isLuxury,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Public API는 ACTIVE만 조회
        var query = new BrandSearchQuery(
                keyword,
                ACTIVE_STATUS, // 강제로 ACTIVE만
                isLuxury,
                department,
                null // country
        );

        var pageResult = searchBrandUseCase.search(query, page, size);
        var apiResponses = pageResult.content().stream()
                .map(brandApiMapper::toApiResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }

    /**
     * 브랜드 간단 목록 (셀렉트박스용)
     *
     * @return ACTIVE 브랜드의 간단 정보 목록
     */
    @GetMapping("/simple-list")
    public ResponseEntity<ApiResponse<List<BrandSimpleApiResponse>>> getSimpleList() {
        var simpleResponses = searchBrandUseCase.getSimpleList();
        var apiResponses = brandApiMapper.toSimpleApiResponseList(simpleResponses);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponses));
    }

    /**
     * 별칭으로 브랜드 조회
     *
     * @param aliasName 별칭명 (원문 또는 정규화된 형태)
     * @param mallCode 몰 코드 (기본: GLOBAL) - 현재 미사용
     * @param sellerId 셀러 ID (선택) - 현재 미사용
     * @return 매칭된 브랜드 후보 리스트
     */
    @GetMapping("/resolve-by-alias")
    public ResponseEntity<ApiResponse<AliasMatchApiResponse>> resolveByAlias(
            @RequestParam String aliasName,
            @RequestParam(defaultValue = "GLOBAL") String mallCode,
            @RequestParam(required = false) Long sellerId) {

        var matchResponse = resolveAliasUseCase.resolveByAlias(aliasName);
        var apiResponse = brandApiMapper.toAliasMatchApiResponse(aliasName, matchResponse);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
