package com.ryuqq.marketplace.adapter.out.persistence.composite.seller.dto;

import java.time.Instant;

/**
 * RefundPolicyDto - 환불 정책 DTO.
 *
 * <p>SellerPolicyCompositeDto 내부에서 사용.
 */
public record RefundPolicyDto(
        Long id,
        Long sellerId,
        String policyName,
        boolean defaultPolicy,
        boolean active,
        int returnPeriodDays,
        int exchangePeriodDays,
        String nonReturnableConditions,
        boolean partialRefundEnabled,
        boolean inspectionRequired,
        int inspectionPeriodDays,
        String additionalInfo,
        Instant createdAt,
        Instant updatedAt) {}
