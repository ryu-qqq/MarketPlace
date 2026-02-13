package com.ryuqq.marketplace.adapter.in.rest.product.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 상품(SKU) 상태 변경 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLDOUT")
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 상태 변경 요청")
public record ChangeProductStatusApiRequest(
        @Schema(
                        description = "변경할 상태 (ACTIVE, INACTIVE, SOLDOUT)",
                        example = "ACTIVE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "변경할 상태는 필수입니다")
                String targetStatus) {}
