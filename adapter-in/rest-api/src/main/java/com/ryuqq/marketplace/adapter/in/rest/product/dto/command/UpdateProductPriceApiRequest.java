package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 상품(SKU) 가격 수정 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param regularPrice 정가
 * @param currentPrice 판매가
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 가격 수정 요청")
public record UpdateProductPriceApiRequest(
        @Schema(description = "정가", example = "100000", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "정가는 필수입니다")
                Integer regularPrice,
        @Schema(
                        description = "판매가 (정가 이하)",
                        example = "90000",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "판매가는 필수입니다")
                Integer currentPrice) {}
