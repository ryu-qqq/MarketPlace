package com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * UpdateProductGroupBasicInfoApiRequest - 상품 그룹 기본 정보 수정 API Request.
 *
 * <p>API-REQ-001: Record 패턴 사용
 *
 * <p>API-VAL-001: jakarta.validation 사용
 */
public record UpdateProductGroupBasicInfoApiRequest(
        @NotBlank(message = "상품 그룹명은 필수입니다") String productGroupName,
        @NotNull(message = "브랜드 ID는 필수입니다") Long brandId,
        @NotNull(message = "카테고리 ID는 필수입니다") Long categoryId,
        @NotNull(message = "배송 정책 ID는 필수입니다") Long shippingPolicyId,
        @NotNull(message = "반품 정책 ID는 필수입니다") Long refundPolicyId) {}
