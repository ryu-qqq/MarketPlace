package com.ryuqq.marketplace.application.productgroup.dto.command;

/**
 * UpdateProductGroupBasicInfoCommand - 상품 그룹 기본 정보 수정 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record UpdateProductGroupBasicInfoCommand(
        long productGroupId,
        String productGroupName,
        long brandId,
        long categoryId,
        long shippingPolicyId,
        long refundPolicyId) {}
