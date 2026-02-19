package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * 상품 그룹 배치 상태 변경 API 요청.
 *
 * <p>API-DTO-001: API Request DTO는 Record로 정의.
 *
 * <p>API-DTO-003: Validation 어노테이션은 API Request에만 적용.
 *
 * @param productGroupIds 상품 그룹 ID 목록
 * @param targetStatus 변경할 상태 ("ACTIVE", "INACTIVE", "SOLDOUT", "DELETED")
 * @author ryu-qqq
 * @since 1.0.0
 */
@Schema(description = "상품 그룹 배치 상태 변경 요청")
public record BatchChangeProductGroupStatusApiRequest(
        @Schema(
                        description = "상품 그룹 ID 목록",
                        example = "[1, 2, 3]",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotEmpty(message = "상품 그룹 ID 목록은 필수입니다")
                List<@NotNull Long> productGroupIds,
        @Schema(
                        description = "변경할 상태 (ACTIVE, INACTIVE, SOLDOUT, DELETED)",
                        example = "ACTIVE",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "변경할 상태는 필수입니다")
                String targetStatus) {}
