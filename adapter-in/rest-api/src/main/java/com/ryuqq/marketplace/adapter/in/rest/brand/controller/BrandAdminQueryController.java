package com.ryuqq.marketplace.adapter.in.rest.brand.controller;

import jakarta.validation.Valid;

import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.BrandSearchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.brand.mapper.BrandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.brand.port.in.query.GetBrandUseCase;
import com.ryuqq.marketplace.application.brand.port.in.query.SearchBrandUseCase;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Brand Admin Query Controller
 *
 * <p>브랜드 관리용 Admin API (조회 작업)
 *
 * <p>GET 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/catalog/brands")
public class BrandAdminQueryController {

    private final GetBrandUseCase getBrandUseCase;
    private final SearchBrandUseCase searchBrandUseCase;
    private final BrandApiMapper brandApiMapper;

    public BrandAdminQueryController(
            GetBrandUseCase getBrandUseCase,
            SearchBrandUseCase searchBrandUseCase,
            BrandApiMapper brandApiMapper) {
        this.getBrandUseCase = getBrandUseCase;
        this.searchBrandUseCase = searchBrandUseCase;
        this.brandApiMapper = brandApiMapper;
    }

    /**
     * 브랜드 목록 조회 (페이징)
     *
     * @param request 검색 조건 (페이징 포함)
     * @return 브랜드 목록 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<BrandApiResponse>>> getBrands(
            @Valid @ModelAttribute BrandSearchApiRequest request) {

        var query = brandApiMapper.toSearchQuery(request);
        var pageResult = searchBrandUseCase.search(query, request.page(), request.size());

        var apiResponses = pageResult.content().stream()
                .map(brandApiMapper::toApiResponse)
                .toList();

        var pageResponse = new PageApiResponse<>(
                apiResponses,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.first(),
                pageResult.last()
        );

        return ResponseEntity.ok(ApiResponse.ofSuccess(pageResponse));
    }

    /**
     * 브랜드 검색
     *
     * @param request 검색 조건
     * @return 검색 결과 (페이징)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageApiResponse<BrandApiResponse>>> searchBrands(
            @Valid @ModelAttribute BrandSearchApiRequest request) {

        var query = brandApiMapper.toSearchQuery(request);
        var pageResult = searchBrandUseCase.search(query, request.page(), request.size());

        var apiResponses = pageResult.content().stream()
                .map(brandApiMapper::toApiResponse)
                .toList();

        var pageResponse = new PageApiResponse<>(
                apiResponses,
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.first(),
                pageResult.last()
        );

        return ResponseEntity.ok(ApiResponse.ofSuccess(pageResponse));
    }

    /**
     * 브랜드 상세 조회
     *
     * @param id 브랜드 ID
     * @return 브랜드 상세 정보 (별칭 포함)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BrandDetailApiResponse>> getBrand(@PathVariable Long id) {
        var brandDetailResponse = getBrandUseCase.getById(id);
        var apiResponse = brandApiMapper.toDetailApiResponse(brandDetailResponse);

        return ResponseEntity.ok(ApiResponse.ofSuccess(apiResponse));
    }
}
