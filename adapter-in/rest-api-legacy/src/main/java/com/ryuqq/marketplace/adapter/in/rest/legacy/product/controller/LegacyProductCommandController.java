package com.ryuqq.marketplace.adapter.in.rest.legacy.product.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.GROUP_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.OPTION;

import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateOptionsUseCase;
import com.ryuqq.marketplace.application.legacy.product.port.in.command.LegacyProductUpdateStockUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 세토프 어드민용 레거시 상품(SKU) 수정 API 컨트롤러.
 *
 * <p>옵션/SKU 전체 교체, 재고 수정 엔드포인트.
 */
@Tag(name = "세토프 어드민용 레거시 - 상품", description = "세토프 어드민용 레거시 상품(SKU) 엔드포인트.")
@RestController
public class LegacyProductCommandController {

    private final LegacyProductUpdateOptionsUseCase legacyProductUpdateOptionsUseCase;
    private final LegacyProductUpdateStockUseCase legacyProductUpdateStockUseCase;
    private final LegacyProductCommandApiMapper legacyProductCommandApiMapper;

    public LegacyProductCommandController(
            LegacyProductUpdateOptionsUseCase legacyProductUpdateOptionsUseCase,
            LegacyProductUpdateStockUseCase legacyProductUpdateStockUseCase,
            LegacyProductCommandApiMapper legacyProductCommandApiMapper) {
        this.legacyProductUpdateOptionsUseCase = legacyProductUpdateOptionsUseCase;
        this.legacyProductUpdateStockUseCase = legacyProductUpdateStockUseCase;
        this.legacyProductCommandApiMapper = legacyProductCommandApiMapper;
    }

    @Operation(summary = "레거시 옵션/SKU 전체 교체", description = "세토프 어드민 호환 상품 옵션과 SKU를 전체 교체합니다.")
    @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")
    @PutMapping(OPTION)
    public ResponseEntity<LegacyApiResponse<Long>> updateOption(
            @Parameter(description = "상품그룹 ID") @PathVariable long productGroupId,
            @Valid @RequestBody List<LegacyCreateOptionRequest> request) {

        legacyProductUpdateOptionsUseCase.execute(
                legacyProductCommandApiMapper.toUpdateProductsCommand(productGroupId, request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }

    @Operation(summary = "레거시 재고 수정", description = "세토프 어드민 호환 상품별 재고를 수정합니다.")
    @PreAuthorize("@legacyAccess.isProductOwnerOrMaster(#productGroupId)")
    @PatchMapping(GROUP_STOCK)
    public ResponseEntity<LegacyApiResponse<Long>> updateStock(
            @Parameter(description = "상품그룹 ID") @PathVariable long productGroupId,
            @Valid @RequestBody List<LegacyUpdateProductStockRequest> request) {

        legacyProductUpdateStockUseCase.execute(
                productGroupId, legacyProductCommandApiMapper.toUpdateStockCommands(request));
        return ResponseEntity.ok(LegacyApiResponse.of(productGroupId));
    }
}
