package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 상품(SKU) 재고 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param stockQuantity 재고 수량
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 재고 수정 요청")
public record UpdateProductStockApiRequest(
        @Schema(
                        description = "재고 수량 (0 이상)",
                        example = "100",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "재고 수량은 필수입니다")
                @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
                Integer stockQuantity) {}
