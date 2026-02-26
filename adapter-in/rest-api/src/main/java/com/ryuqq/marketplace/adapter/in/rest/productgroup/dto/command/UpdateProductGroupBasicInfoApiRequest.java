package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * UpdateProductGroupBasicInfoApiRequest - 상품 그룹 기본 정보 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
@Schema(description = "상품 그룹 기본 정보 수정 요청")
public record UpdateProductGroupBasicInfoApiRequest(
        @Schema(
                        description = "상품 그룹명",
                        example = "나이키 에어맥스 90",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "상품 그룹명은 필수입니다")
                String productGroupName,
        @Schema(description = "브랜드 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "브랜드 ID는 필수입니다")
                Long brandId,
        @Schema(
                        description = "카테고리 ID",
                        example = "100",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "카테고리 ID는 필수입니다")
                Long categoryId,
        @Schema(
                        description = "배송 정책 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "배송 정책 ID는 필수입니다")
                Long shippingPolicyId,
        @Schema(
                        description = "반품 정책 ID",
                        example = "1",
                        requiredMode = Schema.RequiredMode.REQUIRED)
                @NotNull(message = "반품 정책 ID는 필수입니다")
                Long refundPolicyId) {}
