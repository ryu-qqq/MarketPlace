package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.util.List;

/**
 * SellerPolicyCompositeDto - 셀러 정책 Composite DTO.
 *
 * <p>sellerId와 함께 ShippingPolicy, RefundPolicy 목록을 포함.
 *
 * <p>GetSellerForAdminService에서 사용.
 */
public record SellerPolicyCompositeDto(
        Long sellerId,
        List<ShippingPolicyDto> shippingPolicies,
        List<RefundPolicyDto> refundPolicies) {}
