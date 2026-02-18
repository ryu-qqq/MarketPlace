package com.ryuqq.marketplace.adapter.in.rest.product.controller;

import com.ryuqq.authhub.sdk.annotation.RequirePermission;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.product.ProductAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.mapper.ProductCommandApiMapper;
import com.ryuqq.marketplace.application.product.dto.command.BatchChangeProductStatusCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductPriceCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.port.in.command.BatchChangeProductStatusUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductPriceUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ProductCommandController - 상품(SKU) 수정 API.
 *
 * <p>상품 가격, 재고, 상태 변경 엔드포인트를 제공합니다.
 *
 * <p>API-CTR-001: Controller는 @RestController로 정의.
 *
 * <p>API-CTR-004: ResponseEntity&lt;ApiResponse&lt;T&gt;&gt; 래핑 필수.
 *
 * <p>API-CTR-003: UseCase(Port-In) 인터페이스 의존.
 *
 * <p>API-CTR-002: DELETE 메서드 금지 (소프트 삭제는 PATCH).
 *
 * <p>API-CTR-005: Controller에서 @Transactional 금지.
 *
 * <p>API-CTR-007: Controller에 비즈니스 로직 포함 금지.
 *
 * <p>API-CTR-009: @Valid 어노테이션 필수.
 *
 * <p>API-CTR-010: CQRS Controller 분리.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag(name = "상품(SKU) 관리", description = "상품 가격/재고/상태 수정 API")
@RestController
@RequestMapping(ProductAdminEndpoints.PRODUCTS)
public class ProductCommandController {

    private final UpdateProductPriceUseCase updatePriceUseCase;
    private final UpdateProductStockUseCase updateStockUseCase;
    private final BatchChangeProductStatusUseCase batchChangeStatusUseCase;
    private final UpdateProductsUseCase updateProductsUseCase;
    private final ProductCommandApiMapper mapper;
    private final MarketAccessChecker accessChecker;

    /**
     * ProductCommandController 생성자.
     *
     * @param updatePriceUseCase 상품 가격 수정 UseCase
     * @param updateStockUseCase 상품 재고 수정 UseCase
     * @param batchChangeStatusUseCase 상품 배치 상태 변경 UseCase
     * @param updateProductsUseCase 상품 일괄 수정 UseCase
     * @param mapper Command API 매퍼
     * @param accessChecker 접근 권한 검사기
     */
    public ProductCommandController(
            UpdateProductPriceUseCase updatePriceUseCase,
            UpdateProductStockUseCase updateStockUseCase,
            BatchChangeProductStatusUseCase batchChangeStatusUseCase,
            UpdateProductsUseCase updateProductsUseCase,
            ProductCommandApiMapper mapper,
            MarketAccessChecker accessChecker) {
        this.updatePriceUseCase = updatePriceUseCase;
        this.updateStockUseCase = updateStockUseCase;
        this.batchChangeStatusUseCase = batchChangeStatusUseCase;
        this.updateProductsUseCase = updateProductsUseCase;
        this.mapper = mapper;
        this.accessChecker = accessChecker;
    }

    /**
     * 상품 가격 수정 API.
     *
     * <p>상품의 정가, 판매가, 할인가를 수정합니다.
     *
     * @param productId 상품 ID
     * @param request 가격 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상품 가격 수정", description = "상품의 가격 정보를 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productId, 'product:write')")
    @RequirePermission(value = "product:write", description = "상품 가격 수정")
    @PatchMapping(ProductAdminEndpoints.ID + ProductAdminEndpoints.PRICE)
    public ResponseEntity<Void> updatePrice(
            @Parameter(description = "상품 ID", required = true)
                    @PathVariable(ProductAdminEndpoints.PATH_PRODUCT_ID)
                    Long productId,
            @Valid @RequestBody UpdateProductPriceApiRequest request) {

        UpdateProductPriceCommand command = mapper.toCommand(productId, request);
        updatePriceUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 재고 수정 API.
     *
     * <p>상품의 재고 수량을 수정합니다.
     *
     * @param productId 상품 ID
     * @param request 재고 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상품 재고 수정", description = "상품의 재고 수량을 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productId, 'product:write')")
    @RequirePermission(value = "product:write", description = "상품 재고 수정")
    @PatchMapping(ProductAdminEndpoints.ID + ProductAdminEndpoints.STOCK)
    public ResponseEntity<Void> updateStock(
            @Parameter(description = "상품 ID", required = true)
                    @PathVariable(ProductAdminEndpoints.PATH_PRODUCT_ID)
                    Long productId,
            @Valid @RequestBody UpdateProductStockApiRequest request) {

        UpdateProductStockCommand command = mapper.toCommand(productId, request);
        updateStockUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 배치 상태 변경 API (ProductGroup 단위).
     *
     * <p>특정 상품 그룹 내 여러 상품의 상태를 일괄 변경합니다. 현재 인증된 사용자의 셀러 소유권을 검증합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 배치 상태 변경 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상품 배치 상태 변경", description = "상품 그룹 내 여러 상품의 상태를 일괄 변경합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "상태 변경 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "403",
                description = "소유권 검증 실패")
    })
    @PreAuthorize("hasAuthority('product:write')")
    @RequirePermission(value = "product:write", description = "상품 배치 상태 변경")
    @PatchMapping(ProductAdminEndpoints.PRODUCT_GROUP + ProductAdminEndpoints.STATUS)
    public ResponseEntity<Void> batchChangeStatus(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody BatchChangeProductStatusApiRequest request) {

        long sellerId = accessChecker.resolveCurrentSellerId();
        BatchChangeProductStatusCommand command =
                mapper.toCommand(sellerId, productGroupId, request);
        batchChangeStatusUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }

    /**
     * 상품 일괄 수정 API.
     *
     * <p>상품 그룹 하위 상품들의 가격/재고/SKU/정렬 순서를 일괄 수정합니다.
     *
     * @param productGroupId 상품 그룹 ID
     * @param request 상품 일괄 수정 요청 DTO
     * @return 빈 응답 (204 No Content)
     */
    @Operation(summary = "상품 일괄 수정", description = "상품 그룹 하위 상품들의 가격/재고/SKU/정렬을 일괄 수정합니다.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "204",
                description = "수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "404",
                description = "상품을 찾을 수 없음")
    })
    @PreAuthorize("@access.isSellerOwnerOr(#productGroupId, 'product:write')")
    @RequirePermission(value = "product:write", description = "상품 일괄 수정")
    @PatchMapping(ProductAdminEndpoints.PRODUCT_GROUP)
    public ResponseEntity<Void> updateProducts(
            @Parameter(description = "상품 그룹 ID", required = true)
                    @PathVariable(ProductAdminEndpoints.PATH_PRODUCT_GROUP_ID)
                    Long productGroupId,
            @Valid @RequestBody UpdateProductsApiRequest request) {

        UpdateProductsCommand command = mapper.toCommand(productGroupId, request);
        updateProductsUseCase.execute(command);

        return ResponseEntity.noContent().build();
    }
}
