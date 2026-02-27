package com.ryuqq.marketplace.adapter.in.rest.productgroup.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.query.SearchProductGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupExcelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper.ProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupForExcelUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductGroupQueryController - 상품 그룹 조회 API.
 *
 * <p>API-CTR-001: @RestController 어노테이션 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-007: Controller 비즈니스 로직 금지 -> Mapper에서 변환 처리.
 *
 * <p>API-CTR-010: CQRS Controller 분리 (Query 전용).
 *
 * <p>API-CTR-011: List 직접 반환 금지 -> PageApiResponse 페이징 필수.
 *
 * <p>API-CTR-012: URL 경로 소문자 + 복수형 (/product-groups).
 */
@Tag(name = "상품 그룹 조회", description = "상품 그룹 조회 API")
@RestController
@RequestMapping(ProductGroupAdminEndpoints.PRODUCT_GROUPS)
public class ProductGroupQueryController {

    private final SearchProductGroupByOffsetUseCase searchProductGroupByOffsetUseCase;
    private final SearchProductGroupForExcelUseCase searchProductGroupForExcelUseCase;
    private final GetProductGroupUseCase getProductGroupUseCase;
    private final ProductGroupQueryApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    public ProductGroupQueryController(
            SearchProductGroupByOffsetUseCase searchProductGroupByOffsetUseCase,
            SearchProductGroupForExcelUseCase searchProductGroupForExcelUseCase,
            GetProductGroupUseCase getProductGroupUseCase,
            ProductGroupQueryApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.searchProductGroupByOffsetUseCase = searchProductGroupByOffsetUseCase;
        this.searchProductGroupForExcelUseCase = searchProductGroupForExcelUseCase;
        this.getProductGroupUseCase = getProductGroupUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    @Operation(summary = "상품 그룹 목록 조회", description = "상품 그룹 목록을 페이지 기반으로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('product-group:read')")
    @RequirePermission(value = "product-group:read", description = "상품 그룹 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<PageApiResponse<ProductGroupListApiResponse>>> search(
            @Valid SearchProductGroupsApiRequest request) {

        List<Long> effectiveSellerIds = resolveEffectiveSellerIds(request.sellerIds());
        ProductGroupSearchParams searchParams = mapper.toSearchParams(request, effectiveSellerIds);
        ProductGroupPageResult pageResult = searchProductGroupByOffsetUseCase.execute(searchParams);
        PageApiResponse<ProductGroupListApiResponse> response = mapper.toPageResponse(pageResult);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    @Operation(
            summary = "상품 그룹 엑셀 다운로드 조회",
            description = "상품 그룹 목록을 이미지, 상품(SKU), 상세설명, 고시정보를 포함한 풍부한 데이터로 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공")
    })
    @PreAuthorize("@access.hasPermission('product-group:read')")
    @RequirePermission(value = "product-group:read", description = "상품 그룹 엑셀 다운로드 조회")
    @GetMapping(ProductGroupAdminEndpoints.EXCEL)
    public ResponseEntity<ApiResponse<List<ProductGroupExcelApiResponse>>> searchForExcel(
            @Valid SearchProductGroupsApiRequest request) {

        List<Long> effectiveSellerIds = resolveEffectiveSellerIds(request.sellerIds());
        ProductGroupSearchParams searchParams = mapper.toSearchParams(request, effectiveSellerIds);
        List<ProductGroupExcelCompositeResult> results =
                searchProductGroupForExcelUseCase.execute(searchParams);
        List<ProductGroupExcelApiResponse> responses = mapper.toExcelResponses(results);

        return ResponseEntity.ok(ApiResponse.of(responses));
    }

    @Operation(summary = "상품 그룹 상세 조회", description = "상품 그룹 상세 정보를 조회합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품 그룹을 찾을 수 없음")
    })
    @PreAuthorize("@access.hasPermission('product-group:read')")
    @RequirePermission(value = "product-group:read", description = "상품 그룹 상세 조회")
    @GetMapping(ProductGroupAdminEndpoints.ID)
    public ResponseEntity<ApiResponse<ProductGroupDetailApiResponse>> getById(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductGroupAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId) {

        ProductGroupDetailCompositeResult result = getProductGroupUseCase.execute(productGroupId);
        verifySellerOwnership(result.sellerId());
        ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

        return ResponseEntity.ok(ApiResponse.of(response));
    }

    private List<Long> resolveEffectiveSellerIds(List<Long> requestedSellerIds) {
        if (accessChecker.superAdmin()) {
            return requestedSellerIds;
        }
        long currentSellerId = accessChecker.resolveCurrentSellerId();
        return List.of(currentSellerId);
    }

    private void verifySellerOwnership(long resourceSellerId) {
        if (accessChecker.superAdmin()) {
            return;
        }
        long currentSellerId = accessChecker.resolveCurrentSellerId();
        if (currentSellerId != resourceSellerId) {
            throw new AccessDeniedException("해당 상품 그룹에 접근 권한이 없습니다");
        }
    }
}
