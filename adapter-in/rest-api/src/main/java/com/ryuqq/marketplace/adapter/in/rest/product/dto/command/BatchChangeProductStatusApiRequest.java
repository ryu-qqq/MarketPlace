package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 상품(SKU) 배치 상태 변경 API 요청 (ProductGroup 단위).
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param productIds 상품 ID 목록
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLD_OUT")
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 배치 상태 변경 요청")
public record BatchChangeProductStatusApiRequest(
        @Schema(
                        description = "상품 ID 목록",
                        example = "[10, 20, 30]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "상품 ID 목록은 필수입니다")
                List<@NotNull Long> productIds,
        @Schema(
                        description = "변경할 상태 (ACTIVE, INACTIVE, SOLD_OUT)",
                        example = "ACTIVE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "변경할 상태는 필수입니다")
                String targetStatus) {}
